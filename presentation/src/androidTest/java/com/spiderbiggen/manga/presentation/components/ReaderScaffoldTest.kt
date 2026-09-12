package com.spiderbiggen.manga.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToIndex
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeUp
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.spiderbiggen.manga.presentation.theme.MangaReaderTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ReaderScaffoldTest {
    @get:Rule val composeTestRule = createComposeRule()

    @Test
    fun givenAListAtTheFirstItemWhenScaffoldIsShownThenBarsAndContentPaddingAreVisible() {
        setContent()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag(TOP_BAR_TAG, useUnmergedTree = true).assertIsDisplayed()
        composeTestRule.onNodeWithTag(BOTTOM_BAR_TAG, useUnmergedTree = true).assertIsDisplayed()

        val firstItemBounds =
            composeTestRule
                .onNodeWithTag(FIRST_ITEM_TAG, useUnmergedTree = true)
                .getUnclippedBoundsInRoot()
        assertTrue(
            "Expected top padding, but item top was ${firstItemBounds.top}",
            firstItemBounds.top > 0.dp,
        )
    }

    @Test
    fun givenTheListIsAtTheStartWhenItScrollsAwayThenBarsAreHidden() {
        setContent()

        composeTestRule.onNodeWithTag(LIST_TAG, useUnmergedTree = true).performTouchInput {
            swipeUp()
        }

        assertBarsHidden()
    }

    @Test
    fun givenTheListScrolledAwayWhenItReachesTheFinalItemThenBarsAreShown() {
        setContent()

        composeTestRule.onNodeWithTag(LIST_TAG, useUnmergedTree = true).performTouchInput {
            swipeUp()
        }
        assertBarsHidden()

        composeTestRule
            .onNodeWithTag(LIST_TAG, useUnmergedTree = true)
            .performScrollToIndex(ITEM_COUNT - 1)

        assertBarsVisible()
    }

    @Test
    fun givenTheListIsAwayFromAnEdgeWhenScaffoldIsTappedThenBarsToggle() {
        setContent()

        composeTestRule.onNodeWithTag(LIST_TAG, useUnmergedTree = true).performTouchInput {
            swipeUp()
        }
        assertBarsHidden()

        composeTestRule.onNodeWithTag(SCAFFOLD_TAG, useUnmergedTree = true).performClick()
        assertBarsVisible()

        composeTestRule.onNodeWithTag(SCAFFOLD_TAG, useUnmergedTree = true).performClick()
        assertBarsHidden()
    }

    @Test
    fun givenTheListIsAtAnEdgeWhenScaffoldIsTappedThenBarsRemainVisible() {
        setContent()

        composeTestRule.onNodeWithTag(SCAFFOLD_TAG, useUnmergedTree = true).performClick()

        composeTestRule.onNodeWithTag(TOP_BAR_TAG, useUnmergedTree = true).assertIsDisplayed()
        composeTestRule.onNodeWithTag(BOTTOM_BAR_TAG, useUnmergedTree = true).assertIsDisplayed()
    }

    @Test
    fun givenASnackbarHostWhenBarsAreVisibleThenSnackbarIsAboveTheBottomBar() {
        setContent()

        val snackbarBottom =
            composeTestRule
                .onNodeWithTag(SNACKBAR_TAG, useUnmergedTree = true)
                .getUnclippedBoundsInRoot()
                .bottom
        val bottomBarTop =
            composeTestRule
                .onNodeWithTag(BOTTOM_BAR_TAG, useUnmergedTree = true)
                .getUnclippedBoundsInRoot()
                .top

        assertTrue(snackbarBottom <= bottomBarTop)
    }

    private fun assertBarsHidden() {
        composeTestRule.onNodeWithTag(TOP_BAR_TAG, useUnmergedTree = true).assertIsNotDisplayed()
        composeTestRule.onNodeWithTag(BOTTOM_BAR_TAG, useUnmergedTree = true).assertIsNotDisplayed()
    }

    private fun assertBarsVisible() {
        composeTestRule.waitUntil {
            runCatching {
                composeTestRule
                    .onNodeWithTag(TOP_BAR_TAG, useUnmergedTree = true)
                    .assertIsDisplayed()
                composeTestRule
                    .onNodeWithTag(BOTTOM_BAR_TAG, useUnmergedTree = true)
                    .assertIsDisplayed()
            }
                .isSuccess
        }
    }

    private fun setContent() {
        composeTestRule.setContent {
            MangaReaderTheme {
                val lazyListState = rememberLazyListState()
                ReaderScaffold(
                    lazyListState = lazyListState,
                    modifier = Modifier.fillMaxSize().testTag(SCAFFOLD_TAG),
                    topBar = { Box(Modifier.testTag(TOP_BAR_TAG)) { Text("Top bar") } },
                    bottomBar = { Box(Modifier.testTag(BOTTOM_BAR_TAG)) { Text("Bottom bar") } },
                    snackbarHost = {
                        Box(Modifier.size(10.dp).testTag(SNACKBAR_TAG))
                    },
                ) { contentPadding ->
                    ReaderContent(lazyListState, contentPadding)
                }
            }
        }
    }

    @Composable
    private fun ReaderContent(
        lazyListState: LazyListState,
        contentPadding: PaddingValues,
    ) {
        LazyColumn(
            state = lazyListState,
            modifier = Modifier.fillMaxSize().testTag(LIST_TAG),
            contentPadding = contentPadding,
        ) {
            items((0 until ITEM_COUNT).toList()) { index ->
                Box(
                    Modifier.height(100.dp)
                        .then(if (index == 0) Modifier.testTag(FIRST_ITEM_TAG) else Modifier)
                ) {
                    Text("Item $index")
                }
            }
        }
    }

    private companion object {
        const val ITEM_COUNT = 50
        const val FIRST_ITEM_TAG = "reader-first-item"
        const val LIST_TAG = "reader-list"
        const val SCAFFOLD_TAG = "reader-scaffold"
        const val TOP_BAR_TAG = "reader-top-bar"
        const val BOTTOM_BAR_TAG = "reader-bottom-bar"
        const val SNACKBAR_TAG = "reader-snackbar"
    }
}
