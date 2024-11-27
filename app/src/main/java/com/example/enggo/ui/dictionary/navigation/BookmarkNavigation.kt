package com.example.enggo.ui.dictionary.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.example.enggo.ui.dictionary.BookmarkRoute
import com.example.enggo.ui.dictionary.BookmarkScreen

const val BOOKMARK_ROUTE = "bookmark"

fun NavController.navigateToBookmarks(navOptions: NavOptions? = null) {
    navigate(BOOKMARK_ROUTE, navOptions)
}

fun NavGraphBuilder.bookmarkScreen(
    onItemClick: (Int) -> Unit,
    onBackToDictionary: () -> Unit
) {
    composable(BOOKMARK_ROUTE) {
        BookmarkRoute(onItemClick = onItemClick, onBackToDictionary = onBackToDictionary)
    }
}