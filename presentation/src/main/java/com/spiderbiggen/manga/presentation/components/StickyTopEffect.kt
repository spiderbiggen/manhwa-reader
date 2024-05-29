package com.spiderbiggen.manga.presentation.components

import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.currentStateAsState
import kotlinx.collections.immutable.ImmutableCollection

@Composable
fun <T : Any?> StickyTopEffect(items: ImmutableCollection<T>, state: LazyListState) {
    val manuallyScrolled = remember { mutableStateOf(true) }
    val canScrollBackwards = state.canScrollBackward
    val isDragged = state.interactionSource.collectIsDraggedAsState()
    val lifecycle = LocalLifecycleOwner.current.lifecycle.currentStateAsState()
    DisposableEffect(canScrollBackwards, isDragged) {
        if (!canScrollBackwards) {
            manuallyScrolled.value = false
        } else if (isDragged.value) {
            manuallyScrolled.value = true
        }
        onDispose { }
    }
    LaunchedEffect(items) {
        if (!manuallyScrolled.value && lifecycle.value.isAtLeast(Lifecycle.State.RESUMED)) {
            // scroll to top to ensure latest added element gets visible
            state.animateScrollToItem(0)
        }
    }
}
