package com.spiderbiggen.manga.presentation.components

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import coil.imageLoader
import coil.request.ImageRequest

@Composable
fun <T : Any> ListImagePreloader(
    items: List<T>,
    lazyListState: LazyListState,
    visibleCount: Int = 3,
    preloadCount: Int = 5,
) {
    val context = LocalContext.current
    var preloaded by remember { mutableIntStateOf(0) }
    val firstVisibleIndex by remember { derivedStateOf { lazyListState.firstVisibleItemIndex } }
    val limited by remember {
        derivedStateOf {
            val preloadIndex = firstVisibleIndex + visibleCount + preloadCount
            val limited = preloadIndex.coerceAtMost(items.size - 1)
            if (limited < preloaded) preloaded else limited
        }
    }

    LaunchedEffect(context, limited) {
        items.slice((preloaded + 1)..limited.coerceAtMost(items.size - 1)).forEach {
            val request = ImageRequest.Builder(context)
                .data(it)
                .build()
            context.imageLoader.enqueue(request)
        }
        preloaded = limited
    }
}
