package com.example.enggo.ui.dictionary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.enggo.data.service.UserService

class BookmarkViewModelFactory(
    private val userService: UserService,
    private val userId: String
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BookmarkViewModel::class.java)) {
            return BookmarkViewModel(userService, userId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}