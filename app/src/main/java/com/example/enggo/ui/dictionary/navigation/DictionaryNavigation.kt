package com.example.enggo.ui.dictionary.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.enggo.data.AppContainer
import com.example.enggo.ui.dictionary.DictionaryRoute

const val DICTIONARY_ROUTE = "dictionary"

fun NavController.navigateToDictionary(navOptions: NavOptions? = null) {
    navigate(DICTIONARY_ROUTE, navOptions)
}

fun NavGraphBuilder.dictionaryScreen(appContainer: AppContainer, navController: NavController) {
    composable(
        route = "$DICTIONARY_ROUTE?wordIndex={wordIndex}",
        arguments = listOf(
            navArgument("wordIndex") {
                type = NavType.IntType
                defaultValue = -1
            }
        )
    ) { backStackEntry ->
        val wordIndex = backStackEntry.arguments?.getInt("wordIndex") ?: -1
        DictionaryRoute(appContainer = appContainer, navController = navController, wordIndex = wordIndex)
    }
}