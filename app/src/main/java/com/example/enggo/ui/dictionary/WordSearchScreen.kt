package com.example.enggo.ui.dictionary

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.enggo.R
import com.example.enggo.data.AppContainer
import com.example.enggo.data.service.UserService
import com.example.enggo.model.dictionary.Meaning
import com.example.enggo.model.dictionary.WordModel
import com.example.enggo.model.dictionary.WordState
import com.example.enggo.ui.dictionary.navigation.navigateToBookmarks
import com.google.firebase.firestore.FirebaseFirestore

var dictionaryStringBuilder = StringBuilder()
//val mockWordModel = WordModel(
//    word = "apple",
//    meanings = listOf(
//        Meaning(
//            speechPart = "Noun",
//            def = "A round fruit with red or green skin and a whitish interior.",
//            labels = null,
//            example = "I ate an apple for breakfast.",
//            synonyms = listOf("pome", "Malus")
//        ),
//        Meaning(
//            speechPart = "Noun",
//            def = "A round fruit with red or green skin and a whitish interior.",
//            labels = null,
//            example = "I ate an apple for breakfast.",
//            synonyms = listOf("pome", "Malus")
//        ),
//        Meaning(
//            speechPart = "Noun",
//            def = "A round fruit with red or green skin and a whitish interior.",
//            labels = null,
//            example = "I ate an apple for breakfast.",
//            synonyms = listOf("pome", "Malus")
//        ),
//        Meaning(
//            speechPart = "Noun",
//            def = "A round fruit with red or green skin and a whitish interior.",
//            labels = null,
//            example = "I ate an apple for breakfast.",
//            synonyms = listOf("pome", "Malus")
//        )
//    ),
//    wordsetId = "11111"
//)

@Composable
internal fun DictionaryRoute(
    appContainer: AppContainer,
    navController: NavController,
    wordIndex: Int?
) {
    val userService = UserService(FirebaseFirestore.getInstance())
    val dictionaryRepository = appContainer.dictionaryRepository

    val context = LocalContext.current
    val sharedPref = context.getSharedPreferences("EngGoApp", Context.MODE_PRIVATE)
    val currentUserId = sharedPref.getString("currentUserId", "") ?: ""

    val wordViewModel: WordModelViewModel = viewModel(
        factory = WordModelViewModelFactory(dictionaryRepository, userService, currentUserId)
    )

    val bookmarkViewModel: BookmarkViewModel = viewModel(
        factory = BookmarkViewModelFactory(userService, currentUserId)
    )

    LaunchedEffect(Unit) {
        bookmarkViewModel.loadBookmarks()
    }

    WordSearchScreen(
        wordViewModel = wordViewModel,
        bookmarkViewModel = bookmarkViewModel,
        wordIndex = wordIndex,
        navController = navController
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WordSearchScreen(
    wordViewModel: WordModelViewModel,
    wordIndex: Int?,
    bookmarkViewModel: BookmarkViewModel,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    Scaffold (
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.dictionary),
                            style = MaterialTheme.typography.displaySmall
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { navController.navigateToBookmarks() },
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bookmarks,
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
        Column(
            modifier = Modifier.fillMaxWidth().padding(it)
        ) {
            val keyboardController = LocalSoftwareKeyboardController.current
            val suggestions by wordViewModel.suggestions.collectAsState()
            val wordModelState by wordViewModel.wordState.collectAsState()
            var wordModel = wordModelState
            val bookmarkWordState by bookmarkViewModel.bookmarks.collectAsState()

            if (bookmarkWordState.isNotEmpty() && wordIndex != null && wordIndex != -1) {
                val selectedBookmark = bookmarkWordState.getOrNull(wordIndex)
                selectedBookmark?.let {
                    wordModel = WordState(it)
                }
            }

            SearchComponent(
                suggestions = suggestions,
                onSearch = {
                    wordViewModel.prefixMatcher(it)
                    wordViewModel.searcher(it)
                },
                onClear = {
                    wordViewModel.clearSuggestions()
                },
                onDoneActionClick = {
                    keyboardController?.hide()
                },
                onItemClick = {
                    wordViewModel.searcher(it, true)
                    keyboardController?.hide()
                },
                itemContent = {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Result",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(2.dp))
            SearchContent(wordModel.wordModel, wordViewModel)
        }
    }
}

@Composable
fun SearchContent(
    wordModel: WordModel?,
    wordViewModel: WordModelViewModel
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.secondaryContainer)
    ) {

        Column(
            modifier = Modifier.verticalScroll(rememberScrollState())
        ) {
            if (wordModel != null) {
                Text(
                    text = wordModel.word,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(dimensionResource(R.dimen.padding_small))
                        .align(Alignment.CenterHorizontally)
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth()
                    .padding(dimensionResource(R.dimen.padding_small)),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (wordModel != null) {
                    UtilButtons(wordViewModel)
                }
            }

            Column(
                modifier = Modifier.fillMaxWidth()
                    .padding(dimensionResource(R.dimen.padding_small))
            ) {
                if (wordModel != null) {
                    wordModel.meanings?.forEachIndexed { index, meaning ->
                        dictionaryStringBuilder.append(meaning.speechPart).append("\n")
                        Text(
                            text = meaning.speechPart,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Start,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        dictionaryStringBuilder.append("${index + 1}. ${meaning.def}").append("\n")
                        Text(
                            text = "${index + 1}. ${meaning.def}",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (!meaning.labels.isNullOrEmpty()) {
                            val label = meaning.labels.map { label -> label.name }.toString()
                                .removePrefix("[")
                                .removeSuffix("]").replace(",", " •")
                            dictionaryStringBuilder.append(label).append("\n")
                            Text(
                                text = label,
                                fontStyle = FontStyle.Italic,
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.Gray
                            )
                        }
                        if (!meaning.example.isNullOrEmpty()) {
                            val example = "Example: ${meaning.example}"
                            dictionaryStringBuilder.append(example).append("\n")
                            Text(
                                text = example,
                                fontStyle = FontStyle.Italic,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Normal),
                                color = Color.Gray
                            )
                        }
                        if (!meaning.synonyms.isNullOrEmpty()) {
                            val synonym = "Synonym(s): ${
                                meaning.synonyms.toString()
                                    .removePrefix("[")
                                    .removeSuffix("]")
                            }"
                            dictionaryStringBuilder.append(synonym).append("\n")
                            Text(
                                text = synonym,
                                fontStyle = FontStyle.Italic,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Normal),
                                color = Color.Gray
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Divider(
                            thickness = 0.6.dp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                            modifier = Modifier.padding(dimensionResource(R.dimen.padding_small))
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }
            }
        }
    }
}

//@Composable
//@Preview
//fun WordSearchScreenPreview() {
//    WordSearchScreen()
//}
//
//@Composable
//@Preview
//fun SearchContentPreview() {
//    SearchContent(wordModel = mockWordModel)
//}
