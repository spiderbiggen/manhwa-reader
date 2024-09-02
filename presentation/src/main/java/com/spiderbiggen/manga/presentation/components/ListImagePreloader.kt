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
import coil3.ImageLoader
import coil3.request.ImageRequest

@Composable
fun <T : Any?> ListImagePreloader(
    imageLoader: ImageLoader,
    lazyListState: LazyListState,
    items: List<T>,
    preloadCount: Int = 5,
) {
    val context = LocalContext.current
    var loaded by remember { mutableIntStateOf(lazyListState.layoutInfo.visibleItemsInfo.size) }
    val firstVisibleIndex by remember { derivedStateOf { lazyListState.firstVisibleItemIndex } }
    val visibleCount by remember { derivedStateOf { lazyListState.layoutInfo.visibleItemsInfo.size } }
    val limited by remember {
        derivedStateOf {
            val preloadIndex = firstVisibleIndex + visibleCount + preloadCount
            val limited = preloadIndex.coerceAtMost(items.size - 1)
            if (limited < loaded) loaded else limited
        }
    }

    LaunchedEffect(context, imageLoader, limited) {
        items.slice((loaded + 1)..limited.coerceAtMost(items.size - 1)).forEach {
            val request = ImageRequest.Builder(context).data(it).build()
            imageLoader.enqueue(request)
        }
        loaded = limited
    }
}
