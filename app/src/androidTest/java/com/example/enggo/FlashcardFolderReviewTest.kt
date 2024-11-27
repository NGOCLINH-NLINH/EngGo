package com.example.enggo

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.*
import com.example.enggo.model.Flashcard
import com.example.enggo.model.FlashcardFolder
import org.junit.Rule
import org.junit.Test
import com.example.enggo.ui.flashcard.FlashcardFolderView

class FlashcardFolderReviewTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testFolderInfoDisplay() {
        val folder = FlashcardFolder().apply {
            name = "Test Folder"
            addFlashcard(Flashcard("Hello", "Xin chào") )
        }

        composeTestRule.setContent {
            FlashcardFolderView(fcFolder = folder)
        }

        // Kiểm tra tên thư mục hiển thị đúng
        composeTestRule.onNodeWithText("Test Folder").assertExists()

        // Kiểm tra số lượng flashcard hiển thị đúng
        composeTestRule.onNodeWithText("1 / 1").assertExists()
    }


    @Test
    fun testFlashcardListDisplay() {
        val folder = FlashcardFolder().apply {
            name = "Test Folder"
            addFlashcard(Flashcard("Hello", "Xin chào") )
            addFlashcard(Flashcard("Goodbye", "Tạm biệt") )
        }


        composeTestRule.setContent {
            FlashcardFolderView(fcFolder = folder)
        }

        // Kiểm tra danh sách flashcard hiển thị
        composeTestRule.onNodeWithText("Hello").assertExists()
        composeTestRule.onNodeWithText("Xin chào").assertExists()

        composeTestRule.onNodeWithText("Goodbye").assertExists()
        composeTestRule.onNodeWithText("Tạm biệt").assertExists()
    }
}
