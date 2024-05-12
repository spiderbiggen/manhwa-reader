package com.spiderbiggen.manhwa.presentation.components

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.snapshotFlow

@NonRestartableComposable
@Composable
fun <T : Any?> StickyTopEffect(
    items: Collection<T>,
    state: LazyListState,
) {
    LaunchedEffect(items) {
        snapshotFlow { state.firstVisibleItemIndex }
            .collect {
                // Scroll to the top if a new item is added.
                // (But only if user is scrolled to the top already.)
                if (it <= 1) {
                    state.scrollToItem(0)
                }
            }
    }
}
