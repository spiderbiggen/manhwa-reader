package com.spiderbiggen.manga.presentation.components

import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import kotlinx.collections.immutable.ImmutableCollection

@Composable
fun <T : Any?> StickyTopEffect(items: ImmutableCollection<T>, state: LazyListState) {
    var notManuallyScrolled by remember { mutableStateOf(state.firstVisibleItemIndex == 0) }
    val isPressed = state.interactionSource.collectIsPressedAsState()
    LaunchedEffect(state) {
            snapshotFlow { state.firstVisibleItemIndex }
                .collect {
                    if (it == 0) {
                        notManuallyScrolled = true
                    } else if (isPressed.value) {
                        notManuallyScrolled = false
                    }
                }
    }
    LaunchedEffect(items, state) {
        if (notManuallyScrolled) {
            // scroll to top to ensure latest added element gets visible
            state.animateScrollToItem(0)
        }
    }
}
