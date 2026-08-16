package com.spiderbiggen.manga.presentation.ui.manga.list

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.remember
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.spiderbiggen.manga.presentation.theme.MangaReaderTheme
import com.spiderbiggen.manga.presentation.ui.manga.list.model.MangaScreenData
import com.spiderbiggen.manga.presentation.ui.manga.list.model.MangaScreenState
import com.spiderbiggen.manga.presentation.ui.profile.state.ProfileState
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MangaListScreenTest {
    @get:Rule val composeTestRule = createComposeRule()

    @Test
    fun `given no active filters when filter action is clicked then filter options are shown`() {
        setContent(MangaScreenData())

        composeTestRule.onNodeWithContentDescription("Filters").performClick()

        composeTestRule.onNodeWithText("Filters").assertIsDisplayed()
        composeTestRule.onNodeWithText("Unread chapters").assertIsDisplayed()
        composeTestRule.onNodeWithText("Favorite manga").assertIsDisplayed()
    }

    @Test
    fun `given an active filter when clear is clicked then clear action is dispatched`() {
        var action: MangaListAction? = null
        setContent(
            MangaScreenData(
                activeFilters = persistentSetOf(MangaFilter.Unread),
                state = MangaScreenState.Ready(manga = persistentListOf()),
            ),
            onAction = { action = it },
        )

        composeTestRule.onNodeWithContentDescription("Filters, 1 active").performClick()
        composeTestRule.onNodeWithText("Clear").performClick()

        assertEquals(MangaListAction.ClearFilters, action)
    }

    private fun setContent(
        state: MangaScreenData,
        onAction: (MangaListAction) -> Unit = {},
    ) {
        composeTestRule.setContent {
            val snackbarHostState = remember { SnackbarHostState() }
            MangaReaderTheme {
                MangaListScreen(
                    state = state,
                    snackbarHostState = snackbarHostState,
                    profileState = ProfileState.Unauthenticated,
                    onAction = onAction,
                )
            }
        }
    }
}
