package com.spiderbiggen.manga.presentation.ui.manga.list

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.remember
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.spiderbiggen.manga.domain.model.update.ChangeKind
import com.spiderbiggen.manga.presentation.theme.MangaReaderTheme
import com.spiderbiggen.manga.presentation.ui.manga.list.model.ChangeGroupViewData
import com.spiderbiggen.manga.presentation.ui.manga.list.model.MangaScreenData
import com.spiderbiggen.manga.presentation.ui.manga.list.model.MangaScreenState
import com.spiderbiggen.manga.presentation.ui.manga.list.model.ReleaseChangesViewData
import com.spiderbiggen.manga.presentation.ui.manga.list.model.UpdateNoticeViewData
import com.spiderbiggen.manga.presentation.ui.profile.state.ProfileState
import kotlinx.collections.immutable.persistentListOf
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MangaListUpdateNoticeTest {
    @get:Rule val composeTestRule = createComposeRule()

    private val notice =
        UpdateNoticeViewData(
            currentVersion = "1.32.2",
            latestVersion = "1.32.4",
            releaseUrl = "https://github.com/spiderbiggen/manhwa-reader/releases/tag/v1.32.4",
            releases =
                persistentListOf(
                    ReleaseChangesViewData(
                        version = "1.32.4",
                        groups =
                            persistentListOf(
                                ChangeGroupViewData(
                                    ChangeKind.Fix,
                                    persistentListOf("Sort manga overview by last chapter update"),
                                )
                            ),
                    ),
                    ReleaseChangesViewData(version = "1.32.3", groups = persistentListOf()),
                ),
        )

    @Test
    fun givenAnUpdateNoticeThenTheCardShowsBothVersions() {
        setContent(noticeState())

        composeTestRule.onNodeWithText("Update available").assertIsDisplayed()
        composeTestRule
            .onNodeWithText("Version 1.32.4 is out. You have 1.32.2.")
            .assertIsDisplayed()
    }

    @Test
    fun givenNoUpdateNoticeThenNoCardIsShown() {
        setContent(MangaScreenData(state = MangaScreenState.Ready(manga = persistentListOf())))

        composeTestRule.onNodeWithText("Update available").assertDoesNotExist()
    }

    @Test
    fun givenAnUpdateNoticeWhenWhatsNewIsClickedThenTheChangelogIsShown() {
        setContent(noticeState())

        composeTestRule.onNodeWithText("What's new").performClick()

        composeTestRule.onNodeWithText("What's new in 1.32.4").assertIsDisplayed()
        composeTestRule.onNodeWithText("You're on 1.32.2").assertIsDisplayed()
        composeTestRule.onNodeWithText("Bug fixes").assertIsDisplayed()
        composeTestRule
            .onNodeWithText("• Sort manga overview by last chapter update")
            .assertIsDisplayed()
        composeTestRule.onNodeWithText("Maintenance release").assertIsDisplayed()
        composeTestRule.onNodeWithText("View release on GitHub").assertIsDisplayed()
    }

    @Test
    fun givenAnUpdateNoticeWhenDismissIsClickedThenDismissActionIsDispatched() {
        var action: MangaListAction? = null
        setContent(noticeState(), onAction = { action = it })

        composeTestRule.onNodeWithText("Dismiss").performClick()

        assertEquals(MangaListAction.DismissUpdate, action)
    }

    private fun noticeState() =
        MangaScreenData(
            state = MangaScreenState.Ready(manga = persistentListOf()),
            updateNotice = notice,
        )

    private fun setContent(state: MangaScreenData, onAction: (MangaListAction) -> Unit = {}) {
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
