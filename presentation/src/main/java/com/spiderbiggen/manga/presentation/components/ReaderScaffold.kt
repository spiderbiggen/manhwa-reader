package com.spiderbiggen.manga.presentation.components

import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

@Composable
fun ReaderScaffold(
    lazyListState: LazyListState,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    snackbarHost: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit,
) {
    var barsVisible by rememberSaveable { mutableStateOf(true) }

    LaunchedEffect(lazyListState) {
        snapshotFlow { isAtExtreme(lazyListState) to lazyListState.isScrollInProgress }
            .collect { (extreme, scrolling) ->
                when {
                    extreme -> barsVisible = true
                    scrolling -> barsVisible = false
                }
            }
    }

    val density = LocalDensity.current
    var topBarHeightPx by remember { mutableIntStateOf(0) }
    var bottomBarHeightPx by remember { mutableIntStateOf(0) }
    val topContentPadding = with(density) { topBarHeightPx.toDp() }
    val bottomContentPadding = with(density) { bottomBarHeightPx.toDp() }
    Box(
        modifier =
            modifier.background(containerColor).clickable(
                interactionSource = null,
                indication = null,
            ) {
                if (!isAtExtreme(lazyListState)) barsVisible = !barsVisible
            }
    ) {
        content(PaddingValues(top = topContentPadding, bottom = bottomContentPadding))
        AnimatedVisibility(
            visible = barsVisible,
            enter = fadeIn() + slideInVertically { -it },
            exit = fadeOut() + slideOutVertically { -it },
        ) {
            Box(Modifier.onSizeChanged { topBarHeightPx = it.height }) {
                topBar()
            }
        }
        AnimatedVisibility(
            visible = barsVisible,
            enter = fadeIn() + slideInVertically { it },
            exit = fadeOut() + slideOutVertically { it },
            modifier = Modifier.align(Alignment.BottomCenter),
        ) {
            Box(Modifier.onSizeChanged { bottomBarHeightPx = it.height }) {
                bottomBar()
            }
        }
        Box(
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = bottomContentPadding)
        ) {
            snackbarHost()
        }
    }

    SystemBarsVisibilityEffect(barsVisible)
}

@Composable
private fun SystemBarsVisibilityEffect(visible: Boolean) {
    val context = LocalContext.current
    val view = LocalView.current
    DisposableEffect(context, view) {
        onDispose {
            (context as? Activity)
                ?.window
                ?.let { WindowCompat.getInsetsController(it, view) }
                ?.show(WindowInsetsCompat.Type.systemBars())
        }
    }
    LaunchedEffect(context, view, visible) {
        val window = (context as? Activity)?.window ?: return@LaunchedEffect
        val controller = WindowCompat.getInsetsController(window, view)
        if (visible) {
            controller.show(WindowInsetsCompat.Type.systemBars())
        } else {
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            controller.hide(WindowInsetsCompat.Type.systemBars())
        }
    }
}

private fun isAtExtreme(lazyListState: LazyListState): Boolean {
    val atStart =
        lazyListState.firstVisibleItemIndex == 0 && lazyListState.firstVisibleItemScrollOffset == 0
    return atStart || lastItemIsVisible(lazyListState)
}

private fun lastItemIsVisible(lazyListState: LazyListState): Boolean {
    val info = lazyListState.layoutInfo
    val lastVisibleItem = info.visibleItemsInfo.lastOrNull() ?: return false
    return lastVisibleItem.index + 1 >= info.totalItemsCount
}
