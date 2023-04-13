package com.spiderbiggen.manhwa.presentation.components

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import coil.imageLoader
import coil.request.ImageRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun <T : Any> ListImagePreloader(
    items: List<T>,
    lazyListState: LazyListState,
    scope: CoroutineScope = rememberCoroutineScope(),
    visibleCount: Int = 3,
    preloadCount: Int = 10,
    itemTransform: (T) -> Any = { it }
) {
    val maxItems = items.size
    var preloaded by remember { mutableStateOf(0) }
    val context = LocalContext.current

    LaunchedEffect(items) {
        lazyListState.interactionSource.interactions.collect {
            val preloadIndex = lazyListState.firstVisibleItemIndex + visibleCount + preloadCount
            val limited = preloadIndex.coerceAtMost(maxItems - 1)

            items.slice((preloaded + 1)..limited).takeIf { it.isNotEmpty() }?.let {
                for (image in it) {
                    scope.launch {
                        val request = ImageRequest.Builder(context)
                            .data(itemTransform(image))
                            .build()
                        context.imageLoader.enqueue(request)
                    }
                }
                preloaded = preloadIndex
            }
        }
    }
}