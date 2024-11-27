package com.example.enggo.ui.dictionary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.enggo.data.service.UserService
import com.example.enggo.model.dictionary.WordModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class BookmarkViewModel(
    private val userService: UserService,
    private val userId: String
) : ViewModel() {
    private val _bookmarks = MutableStateFlow<List<WordModel>>(emptyList())
    val bookmarks: StateFlow<List<WordModel>> get() = _bookmarks

    fun loadBookmarks() {
        viewModelScope.launch(Dispatchers.IO) {
            val bookmarkList = userService.getBookmarks(userId)
            _bookmarks.value = bookmarkList
        }
    }

    fun removeBookmark(wordsetId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            userService.removeBookmark(userId, wordsetId)
            loadBookmarks()
        }
    }
}