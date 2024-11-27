package com.example.enggo.ui.dictionary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.enggo.data.dictionary.DictionaryBaseRepository
import com.example.enggo.data.service.UserService


class WordModelViewModelFactory(
    private val dictRepository: DictionaryBaseRepository,
    private val userService: UserService,
    private val currentUserId: String
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WordModelViewModel::class.java)) {
            return WordModelViewModel(dictRepository, userService, currentUserId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}