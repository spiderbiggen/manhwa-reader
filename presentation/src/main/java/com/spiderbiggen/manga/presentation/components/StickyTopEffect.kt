package com.spiderbiggen.manga.presentation.components

import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.collections.immutable.ImmutableCollection

@Composable
fun <T : Any?> StickyTopEffect(items: ImmutableCollection<T>, state: LazyListState) {
    var manuallyScrolled by remember { mutableStateOf(state.firstVisibleItemIndex == 0) }
    val canScrollBackwards = state.canScrollBackward
    val isDragged by state.interactionSource.collectIsDraggedAsState()
    DisposableEffect(canScrollBackwards, isDragged) {
        if (!canScrollBackwards) {
            manuallyScrolled = false
        } else if (isDragged) {
            manuallyScrolled = true
        }
        onDispose { }
    }
    LaunchedEffect(items, state) {
        if (!manuallyScrolled) {
            // scroll to top to ensure latest added element gets visible
            state.animateScrollToItem(0)
        }
    }
}

private data class ScrollState(
    val index: Int,
    val offset: Int,
    val interaction: Interaction,
)
