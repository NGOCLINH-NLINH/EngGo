package com.example.enggo.ui.dictionary

import android.content.Context
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.enggo.R
import com.example.enggo.data.service.UserService
import com.example.enggo.model.dictionary.WordModel
import com.example.enggo.ui.dictionary.navigation.navigateToBookmarks
import com.google.firebase.firestore.FirebaseFirestore

@Composable
internal fun BookmarkRoute(
    onItemClick: (Int) -> Unit,
    onBackToDictionary: () -> Unit
) {
    val userService = UserService(FirebaseFirestore.getInstance())

    val context = LocalContext.current
    val sharedPref = context.getSharedPreferences("EngGoApp", Context.MODE_PRIVATE)
    val currentUserId = sharedPref.getString("currentUserId", "") ?: ""

    val bookmarkViewModel: BookmarkViewModel = viewModel(
        factory = BookmarkViewModelFactory(userService = userService, userId = currentUserId)
    )

    LaunchedEffect(Unit) {
        bookmarkViewModel.loadBookmarks()
    }

    BookmarkScreen(
        viewModel = bookmarkViewModel,
        onItemClick = onItemClick,
        onBackToDictionary = onBackToDictionary)
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookmarkScreen(
    viewModel: BookmarkViewModel,
    onItemClick: (Int) -> Unit,
    onBackToDictionary: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bookmarks by viewModel.bookmarks.collectAsState()

    Scaffold (
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.bookmarks),
                            style = MaterialTheme.typography.displaySmall
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = { onBackToDictionary() },
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBackIosNew,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                modifier = modifier
            )
        },
        contentWindowInsets = ScaffoldDefaults
            .contentWindowInsets
            .exclude(WindowInsets.navigationBars)
            .exclude(WindowInsets.ime),
        containerColor = MaterialTheme.colorScheme.background,
        modifier = Modifier.padding(dimensionResource(R.dimen.padding_small))
    ) { it ->
        Box(modifier = Modifier.fillMaxSize().padding(it)) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                BookmarkList(list = bookmarks, onItemClick = onItemClick) {
                    viewModel.removeBookmark(it.wordsetId)
                    Log.d("BOOKMARK", "da nhan icon xoa bookmark")
                }
            }
            if (bookmarks.isEmpty()) {
                Text(
                    text = "Bookmark is empty",
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier
                        .padding(dimensionResource(R.dimen.padding_medium))
                        .align(Alignment.Center)
                )
            }
        }
    }

}

@Composable
fun BookmarkList(
    list: List<WordModel>,
    onItemClick: (Int) -> Unit,
    onDeleteClick: (WordModel) -> Unit
) {
    LazyColumn(modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.padding_medium))) {
        itemsIndexed(list) { index, item ->
            BookmarkItem(index, wordModel = item, onItemClick, onDeleteClick)
        }
    }
}

@Composable
fun BookmarkItem(
    index: Int,
    wordModel: WordModel,
    onItemClick: (Int) -> Unit,
    onDeleteClick: (WordModel) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = dimensionResource(R.dimen.padding_small))
            .wrapContentHeight()
            .clickable {
                onItemClick(index)
            },
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.secondaryContainer),
    ) {
        Row(
            modifier = Modifier.padding(dimensionResource(R.dimen.padding_small)),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.weight(4f)
            ) {
                Text(
                    text = wordModel.word,
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Start,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "1. ${wordModel.meanings?.get(0)?.def}",
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Start,
                    fontWeight = FontWeight.Normal,
                )
                wordModel.meanings?.get(0)?.synonyms?.let {
                    Text(
                        text = "Synonym(s): ${
                            it.toString().removePrefix("[")
                                .removeSuffix("]")
                        }",
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Start,
                        fontStyle = FontStyle.Italic,
                        fontWeight = FontWeight.Normal,
                    )
                }
                wordModel.meanings?.get(0)?.example?.let {
                    Text(
                        text = "Ex: $it",
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Start,
                        fontStyle = FontStyle.Italic,
                        fontWeight = FontWeight.Normal,
                    )
                }
            }

            IconButton(
                onClick = { onDeleteClick(wordModel) },
                modifier = Modifier.weight(.5f)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.delete),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

//@Preview
//@Composable
//fun BookmarkScreenPreview() {
//    BookmarkScreen(
//        viewModel = BookmarkViewModel()
//    )
//}