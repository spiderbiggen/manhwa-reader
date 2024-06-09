package com.spiderbiggen.manga.presentation.components

import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import kotlinx.collections.immutable.ImmutableCollection

@Composable
fun <T : Any?> StickyTopEffect(items: ImmutableCollection<T>, listState: LazyListState) {
    var manuallyScrolled: Boolean by rememberSaveable { mutableStateOf(false) }
    val canScrollBackwards by remember { derivedStateOf { listState.canScrollBackward } }
    val isDragged by listState.interactionSource.collectIsDraggedAsState()
    LaunchedEffect(canScrollBackwards, isDragged) {
        if (!canScrollBackwards) {
            manuallyScrolled = false
        } else if (isDragged) {
            manuallyScrolled = true
        }
    }
    LaunchedEffect(items, manuallyScrolled) {
        if (!manuallyScrolled) {
            // scroll to top to ensure latest added element gets visible
            listState.animateScrollToItem(0)
        }
    }
}
