package com.spiderbiggen.manga.presentation.components.bottomappbar

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.DecayAnimationSpec
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.BottomAppBarScrollBehavior
import androidx.compose.material3.BottomAppBarState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.rememberBottomAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.unit.Velocity

@ExperimentalMaterial3Api
@ExperimentalMaterial3ExpressiveApi
@Composable
fun BottomAppBarDefaults.scrollAgainstContentBehavior(
    state: BottomAppBarState = rememberBottomAppBarState(),
    canScroll: () -> Boolean = { true },
    lastItemIsVisible: () -> Boolean = { false },
): BottomAppBarScrollBehavior = remember(state, canScroll, lastItemIsVisible) {
    ExitAlwaysScrollBehavior(
        state = state,
        canScroll = canScroll,
        lastItemIsVisible = lastItemIsVisible,
    )
}

@ExperimentalMaterial3Api
private class ExitAlwaysScrollBehavior(
    override val state: BottomAppBarState,
    val canScroll: () -> Boolean = { true },
    val lastItemIsVisible: () -> Boolean = { false },
) : BottomAppBarScrollBehavior {
    override val flingAnimationSpec: DecayAnimationSpec<Float>? = null
    override val snapAnimationSpec: AnimationSpec<Float>? = null
    override val isPinned: Boolean = false

    override var nestedScrollConnection = object : NestedScrollConnection {
        override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
            if (!canScroll()) return Offset.Zero

            if (lastItemIsVisible() && available.y < 0f) {
                state.contentOffset -= available.y
                state.heightOffset -= available.y
            } else {
                state.contentOffset += available.y
                state.heightOffset += available.y
            }
            return Offset.Zero
        }

        override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
            if (
                available.y > 0f &&
                (state.heightOffset == 0f || state.heightOffset == state.heightOffsetLimit)
            ) {
                // Reset the total content offset to zero when scrolling all the way down.
                // This will eliminate some float precision inaccuracies.
                state.contentOffset = 0f
            }
            return super.onPostFling(consumed, available)
        }
    }
}

internal fun lastItemIsVisible(lazyListState: LazyListState): Boolean {
    val info = lazyListState.layoutInfo
    val lastVisibleItem = info.visibleItemsInfo.lastOrNull() ?: return false

    val count = info.totalItemsCount
    return lastVisibleItem.index + 1 >= count
}
