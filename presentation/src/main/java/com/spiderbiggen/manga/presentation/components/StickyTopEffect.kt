package com.spiderbiggen.manga.presentation.components

import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import kotlinx.collections.immutable.ImmutableCollection

@Composable
fun rememberManualScrollState(listState: LazyListState): State<Boolean> = rememberManualScrollState(
    listState.interactionSource,
    hasItems = { listState.layoutInfo.totalItemsCount > 0 },
    canScrollBackwards = { listState.canScrollBackward },
)

@Composable
private fun rememberManualScrollState(
    interactionSource: InteractionSource,
    hasItems: () -> Boolean,
    canScrollBackwards: () -> Boolean,
): State<Boolean> {
    val manuallyScrolled = rememberSaveable { mutableStateOf(false) }
    val isDragged by interactionSource.collectIsDraggedAsState()
    SideEffect {
        when {
            hasItems() && !canScrollBackwards() -> manuallyScrolled.value = false
            isDragged -> manuallyScrolled.value = true
        }
    }
    return manuallyScrolled
}

@Composable
fun <T> StickyTopEffect(items: ImmutableCollection<T>, listState: LazyListState, isManuallyScrolled: () -> Boolean) {
    LaunchedEffect(items) {
        if (!isManuallyScrolled()) {
            // scroll to top to ensure latest added element gets visible
            listState.animateScrollToItem(0)
        }
    }
}
