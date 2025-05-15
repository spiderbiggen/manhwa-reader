package com.spiderbiggen.manga.presentation.components

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.MutableWindowInsets
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.onConsumedWindowInsetsChanged
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.unit.offset
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.util.fastMap
import androidx.compose.ui.util.fastMaxBy

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MangaScaffold(
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    snackbarHost: @Composable () -> Unit = {},
    containerColor: Color = MaterialTheme.colorScheme.background,
    contentColor: Color = contentColorFor(containerColor),
    contentWindowInsets: WindowInsets = ScaffoldDefaults.contentWindowInsets,
    topBarOffset: () -> Int = { 0 },
    bottomBarOffset: () -> Int = { 0 },
    content: @Composable (PaddingValues) -> Unit,
) {
    val safeInsets = remember(contentWindowInsets) { MutableWindowInsets(contentWindowInsets) }
    Surface(
        modifier = modifier.onConsumedWindowInsetsChanged { consumedWindowInsets ->
            // Exclude currently consumed window insets from user provided contentWindowInsets
            safeInsets.insets = contentWindowInsets.exclude(consumedWindowInsets)
        },
        color = containerColor,
        contentColor = contentColor,
    ) {
        MangaScaffoldLayout(
            topBar = topBar,
            bottomBar = bottomBar,
            snackbarHost = snackbarHost,
            topBarOffset = topBarOffset,
            bottomBarOffset = bottomBarOffset,
            contentWindowInsets = safeInsets,
            content = content,
        )
    }
}

@Composable
fun MangaScaffoldLayout(
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit,
    snackbarHost: @Composable () -> Unit,
    bottomBar: @Composable () -> Unit,
    topBarOffset: () -> Int,
    bottomBarOffset: () -> Int,
    contentWindowInsets: WindowInsets,
    content: @Composable (PaddingValues) -> Unit,
) {
    SubcomposeLayout(modifier) { constraints ->
        val layoutWidth = constraints.maxWidth
        val layoutHeight = constraints.maxHeight

        val looseConstraints = constraints.copy(minWidth = 0, minHeight = 0)

        val topBarPlaceables = subcompose(ScaffoldLayoutContent.TopBar, topBar).fastMap {
            it.measure(looseConstraints)
        }

        val topOffset = topBarOffset()
        val topBarHeight = (topBarPlaceables.fastMaxBy { it.height }?.height ?: 0)
            .plus(topOffset)
            .coerceAtLeast(0)

        val snackbarPlaceables = subcompose(ScaffoldLayoutContent.Snackbar, snackbarHost).fastMap {
            // respect only bottom and horizontal for snackbar and fab
            val leftInset = contentWindowInsets.getLeft(this, layoutDirection)
            val rightInset = contentWindowInsets.getRight(this, layoutDirection)
            val bottomInset = contentWindowInsets.getBottom(this)
            // offset the snackbar constraints by the insets values
            it.measure(looseConstraints.offset(-leftInset - rightInset, -bottomInset))
        }

        val snackbarHeight = snackbarPlaceables.fastMaxBy { it.height }?.height ?: 0
        val snackbarWidth = snackbarPlaceables.fastMaxBy { it.width }?.width ?: 0

        val bottomBarPlaceables = subcompose(ScaffoldLayoutContent.BottomBar) { bottomBar() }
            .fastMap { it.measure(looseConstraints) }

        val bottomBarHeight = bottomBarPlaceables.fastMaxBy { it.height }?.height
            ?.minus(bottomBarOffset())
            ?.coerceAtLeast(0)

        val snackbarOffsetFromBottom = if (snackbarHeight != 0) {
            snackbarHeight + (bottomBarHeight ?: contentWindowInsets.getBottom(this))
        } else {
            0
        }

        val contentHeight = layoutHeight - topBarHeight - (bottomBarHeight ?: 0)
        val bodyContentPlaceables = subcompose(ScaffoldLayoutContent.MainContent) {
            val insets = contentWindowInsets.asPaddingValues(this)
            val innerPadding = PaddingValues(
                top = if (topBarPlaceables.isEmpty()) {
                    insets.calculateTopPadding()
                } else {
                    0.toDp()
                },
                bottom = if (bottomBarPlaceables.isEmpty() || bottomBarHeight == 0 || bottomBarHeight == null) {
                    insets.calculateBottomPadding()
                } else {
                    0.toDp()
                },
                start = insets.calculateStartPadding(layoutDirection),
                end = insets.calculateEndPadding(layoutDirection),
            )
            content(innerPadding)
        }
            .fastMap { it.measure(looseConstraints.copy(maxHeight = contentHeight)) }

        layout(layoutWidth, layoutHeight) {
            // Placing to control drawing order to match default elevation of each placeable
            bodyContentPlaceables.fastForEach { it.place(0, topBarHeight) }
            topBarPlaceables.fastForEach { it.place(0, topOffset) }
            snackbarPlaceables.fastForEach {
                it.place(
                    (layoutWidth - snackbarWidth) / 2 +
                        contentWindowInsets.getLeft(this@SubcomposeLayout, layoutDirection),
                    layoutHeight - snackbarOffsetFromBottom,
                )
            }
            // The bottom bar is always at the bottom of the layout
            bottomBarPlaceables.fastForEach { it.place(0, layoutHeight - (bottomBarHeight ?: 0)) }
        }
    }
}

private enum class ScaffoldLayoutContent {
    TopBar,
    MainContent,
    Snackbar,
    Fab,
    BottomBar
}
