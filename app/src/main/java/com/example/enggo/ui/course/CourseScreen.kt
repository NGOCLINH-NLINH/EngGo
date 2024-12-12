package com.example.enggo.ui.course

import android.content.Context
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.enggo.R
import com.example.enggo.data.service.CourseService
import com.example.enggo.data.service.UserService
import com.example.enggo.model.course.Course
import com.example.enggo.ui.theme.EngGoTheme
import com.google.firebase.firestore.FirebaseFirestore

enum class LevelTitles(val level: Int, val title: String) {
    ELEMENTARY(1, "Elementary Courses"),
    PRE_INTERMEDIATE(2, "Pre-Intermediate Courses"),
    INTERMEDIATE(3, "Intermediate Courses"),
    INTERMEDIATE_PLUS(4, "Intermediate Plus Courses"),
    UPPER_INTERMEDIATE(5, "Upper-Intermediate Courses"),
    ADVANCED(6, "Advanced Courses"),
    IELTS(7, "IELTS Courses");

    fun getShortTitle(): String {
        return when (this) {
            ELEMENTARY -> "Elementary"
            INTERMEDIATE -> "Intermediate"
            INTERMEDIATE_PLUS -> "Intermediate Plus"
            UPPER_INTERMEDIATE -> "Upper Intermediate"
            ADVANCED -> "Advanced"
            else -> this.title
        }
    }
}

@Composable
internal fun CourseRoute(
    onCourseClick: (Int, String) -> Unit,
) {
    // TODO()
    val courseService = CourseService(FirebaseFirestore.getInstance())
    val userService = UserService(FirebaseFirestore.getInstance())

    val context = LocalContext.current
    val sharedPref = context.getSharedPreferences("EngGoApp", Context.MODE_PRIVATE)
    val currentUserId = sharedPref.getString("currentUserId", "") ?: ""
    val courseViewModel: CourseViewModel = viewModel(factory = CourseViewModelFactory(courseService, userService))

    val onItemClick: (Int, String) -> Unit = { courseId, courseName ->
        onCourseClick(courseId, courseName)

        currentUserId.let {
            val course = Course(course_id = courseId, course_name = courseName, course_description = "")
            courseViewModel.checkAndUpdateRecentCourses(it, course)
        }
    }

    val unfilteredCourses by courseViewModel.courses.collectAsState()

    CourseScreen(
        //onCourseClick = onCourseClick,
        onCourseClick = onItemClick,
        unfilteredCoursesList = unfilteredCourses
    )
}

@Composable
fun CourseScreen(
    onCourseClick: (Int, String) -> Unit,
    unfilteredCoursesList: List<Course>,
    modifier: Modifier = Modifier,
) {
    // TODO()

    val groupedCourses = unfilteredCoursesList.groupBy { it.course_level }


    Scaffold(
        topBar = {
            CoursesTopAppBar()
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)
            .verticalScroll(rememberScrollState())
        ) {
            LevelTitles.values().forEach { level ->
                val coursesForLevel = groupedCourses[level.level]
                if (!coursesForLevel.isNullOrEmpty()) {
                    Text(
                        text = level.title,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.padding_medium)),
                    )
                    CourseListRow(
                        coursesList = coursesForLevel,
                        onItemClick = onCourseClick,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoursesTopAppBar(modifier: Modifier = Modifier) {
    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.courses),
                    style = MaterialTheme.typography.displaySmall
                )
            }
        },
        modifier = modifier
    )
}

@Composable
fun CourseListRow(
    coursesList: List<Course>,
    onItemClick: (Int, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    LazyRow(
        state = listState,
        modifier = Modifier
            .fillMaxWidth(),
            //.padding(horizontal = dimensionResource(R.dimen.padding_medium)),
        contentPadding = PaddingValues(horizontal = dimensionResource(R.dimen.padding_medium)),
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small)),
        userScrollEnabled = true
    ) {
        items(coursesList, key = { course -> course.course_id }) { course ->
            CourseListItem(
                course = course,
                onItemClick = onItemClick,
                modifier = Modifier
                    .fillParentMaxWidth()
                    .height(128.dp) // TODO: create dimens value
            )
        }
    }


    // TODO: need check
    LaunchedEffect(listState.firstVisibleItemIndex) {
        val firstVisibleIndex = listState.firstVisibleItemIndex
        val visibleItemOffset = listState.layoutInfo.visibleItemsInfo.firstOrNull()?.offset
        if (visibleItemOffset != null && visibleItemOffset > 0) {
            listState.animateScrollToItem(firstVisibleIndex + 1)
        }
    }
}

@Composable
fun CourseListItem(
    course: Course,
    onItemClick: (Int, String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        elevation = CardDefaults.cardElevation(),
        modifier = modifier,
        shape = RoundedCornerShape(dimensionResource(R.dimen.card_corner_radius)),
        onClick = { onItemClick(course.course_id, course.course_name) }
    ) {
        Row (
            modifier = Modifier.fillMaxWidth()
                .size(128.dp) // TODO: create dimens value
        ){
            CourseListImageItem(
                courseId = course.course_id,
                modifier = Modifier.size(128.dp) // TODO: create dimens value
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .size(128.dp) // TODO: create dimens value
                    .padding(
                        top = dimensionResource(R.dimen.padding_medium),
                        end = dimensionResource(R.dimen.padding_medium),
                        bottom = dimensionResource(R.dimen.padding_medium),
                    )
            ) {
                Text(
                    text = course.course_name,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 4.dp) // TODO: create dimens value
                )
                course.course_description.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 2
                    )
                }
                Spacer(Modifier.weight(1f))
                Row (
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val levelTitle = LevelTitles.values().firstOrNull { it.level == course.course_level }?.getShortTitle() ?: "Unknown"
                    Text(
                        text = levelTitle,
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(Modifier.weight(1f))
                    Icon(
                        imageVector = Icons.Filled.ChevronRight,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
    }
}

@Composable
fun CourseListImageItem(
    courseId: Int,
    modifier: Modifier = Modifier
) {
    val courseImages = listOf(
        R.drawable.course_1,
        R.drawable.course_2,
        R.drawable.course_3,
        R.drawable.course_4,
        R.drawable.course_5,
        R.drawable.course_6,
        R.drawable.course_7,
        R.drawable.course_8,
        R.drawable.course_9,
        R.drawable.course_10
    )

    val imageIndex = courseId % courseImages.size // TODO() set something for fun
    val randomImage = courseImages[imageIndex]

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = randomImage),
            contentDescription = null,
            alignment = Alignment.Center,
            contentScale = ContentScale.Fit,
            modifier = Modifier.padding(dimensionResource(R.dimen.padding_medium))
                .align(Alignment.Center)
        )
    }
}

