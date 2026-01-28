package com.spiderbiggen.manga.presentation.components

import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import kotlinx.collections.immutable.ImmutableCollection

@Composable
fun rememberManualScrollState(listState: LazyListState): Boolean = rememberManualScrollState(
    listState.interactionSource,
    hasItemsProvider = { listState.layoutInfo.totalItemsCount > 0 },
    canScrollBackwardsProvider = { listState.canScrollBackward },
)

@Composable
fun rememberManualScrollState(gridState: LazyGridState): Boolean = rememberManualScrollState(
    gridState.interactionSource,
    hasItemsProvider = { gridState.layoutInfo.totalItemsCount > 0 },
    canScrollBackwardsProvider = { gridState.canScrollBackward },
)

@Composable
private fun rememberManualScrollState(
    interactionSource: InteractionSource,
    hasItemsProvider: () -> Boolean,
    canScrollBackwardsProvider: () -> Boolean,
): Boolean {
    var manuallyScrolled: Boolean by rememberSaveable { mutableStateOf(false) }
    val isDragged by interactionSource.collectIsDraggedAsState()
    SideEffect {
        when {
            hasItemsProvider() && !canScrollBackwardsProvider() -> manuallyScrolled = false
            isDragged -> manuallyScrolled = true
        }
    }
    return manuallyScrolled
}

@Composable
fun <T : Any?> StickyTopEffect(
    items: ImmutableCollection<T>,
    listState: LazyListState,
    isManuallyScrolled: Boolean = rememberManualScrollState(listState),
) {
    LaunchedEffect(items, isManuallyScrolled) {
        if (!isManuallyScrolled) {
            // scroll to top to ensure latest added element gets visible
            listState.animateScrollToItem(0)
        }
    }
}

@Composable
fun <T : Any?> StickyTopEffect(
    items: ImmutableCollection<T>,
    gridState: LazyGridState,
    isManuallyScrolled: Boolean = rememberManualScrollState(gridState),
) {
    LaunchedEffect(items, isManuallyScrolled) {
        if (!isManuallyScrolled) {
            // scroll to top to ensure latest added element gets visible
            gridState.animateScrollToItem(0)
        }
    }
}
