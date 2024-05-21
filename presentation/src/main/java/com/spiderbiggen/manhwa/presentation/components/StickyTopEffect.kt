package com.spiderbiggen.manhwa.presentation.components

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember

@Composable
fun <T : Any?> StickyTopEffect(items: Collection<T>, state: LazyListState) {
    val firstVisibleItemIndex by remember { derivedStateOf { state.firstVisibleItemIndex } }
    LaunchedEffect(items) {
        if (firstVisibleItemIndex <= 1) {
            //scroll to top to ensure latest added element gets visible
            state.requestScrollToItem(0)
        }
    }
}
