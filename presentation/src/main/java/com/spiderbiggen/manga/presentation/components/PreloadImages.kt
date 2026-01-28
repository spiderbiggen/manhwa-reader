package com.spiderbiggen.manga.presentation.components

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateSetOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.retain.retain
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateSet
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalWindowInfo
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.request.Disposable
import coil3.request.ImageRequest
import coil3.size.Dimension
import coil3.size.Precision
import coil3.size.Size
import coil3.size.SizeResolver
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.ExperimentalCoroutinesApi

@Composable
fun PreloadImages(lazyListState: LazyListState, items: ImmutableList<String>, preloadCount: Int = 5) {
    val width = LocalWindowInfo.current.containerSize.width
    val sizeResolver by rememberUpdatedState {
        SizeResolver(
            Size(
                width = width,
                Dimension.Undefined,
            ),
        )
    }
    PreloadImages(lazyListState, items, sizeResolver, preloadCount)
}

@Composable
fun PreloadImages(
    lazyListState: LazyListState,
    items: ImmutableList<String>,
    sizeResolver: () -> SizeResolver,
    preloadCount: Int = 5,
) {
    PreloadImagesInternal(
        firstVisibleItemIndex = { lazyListState.firstVisibleItemIndex },
        visibleItemsCount = { lazyListState.layoutInfo.visibleItemsInfo.size },
        items = items,
        sizeResolver = sizeResolver,
        preloadCount = preloadCount,
    )
}

@Composable
fun PreloadImages(
    lazyGridState: LazyGridState,
    items: ImmutableList<String>,
    sizeResolver: () -> SizeResolver,
    preloadCount: Int = 5,
) {
    PreloadImagesInternal(
        firstVisibleItemIndex = { lazyGridState.firstVisibleItemIndex },
        visibleItemsCount = { lazyGridState.layoutInfo.visibleItemsInfo.size },
        items = items,
        sizeResolver = sizeResolver,
        preloadCount = preloadCount,
    )
}

@Composable
private fun PreloadImagesInternal(
    firstVisibleItemIndex: () -> Int,
    visibleItemsCount: () -> Int,
    items: ImmutableList<String>,
    sizeResolver: () -> SizeResolver,
    preloadCount: Int = 5,
) {
    if (items.isEmpty() || preloadCount <= 0) return

    val state = retain(items) {
        PreloadImagesState(items)
    }
    DisposableEffect(items) {
        onDispose { state.disposeActive() }
    }

    val range by remember(items, preloadCount) {
        derivedStateOf {
            computeWindow(
                firstVisibleItemIndex = firstVisibleItemIndex(),
                visibleItemsCount = visibleItemsCount(),
                lastIndex = items.lastIndex,
                preloadCount = preloadCount,
            )
        }
    }

    val context = LocalContext.current
    LaunchedEffect(context, items, range) {
        val imageLoader = SingletonImageLoader.get(context)
        state.preload(
            context = context,
            desiredIndices = range,
            imageLoader = imageLoader,
            sizeResolver = sizeResolver(),
        )

        state.disposeUndesired(range)
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
private class PreloadImagesState(
    private val items: ImmutableList<String>,
    private val states: SnapshotStateList<PreloadState>,
    private val activeIndices: SnapshotStateSet<Int> = mutableStateSetOf(),
) {
    constructor(items: ImmutableList<String>) :
        this(
            items,
            SnapshotStateList<PreloadState>(items.size) { PreloadState.Idle },
        )

    fun disposeActive() {
        val snapshot = activeIndices.toSet()
        for (index in snapshot) {
            val value = states[index]
            if (value is PreloadState.Loading) {
                value.disposable.dispose()
                states[index] = PreloadState.Idle
            }
        }
        activeIndices.clear()
    }

    /**
     * Ensure desired indices are enqueued (or marked as success).
     */
    fun preload(
        context: PlatformContext,
        desiredIndices: IntRange,
        imageLoader: ImageLoader,
        sizeResolver: SizeResolver,
    ) {
        for (index in desiredIndices) {
            when (val existing = states[index]) {
                is PreloadState.Success -> Unit

                is PreloadState.Loading -> {
                    val job = existing.disposable.job
                    if (job.isActive) {
                        continue
                    }

                    val hasImage = runCatching { job.getCompleted().image != null }.getOrDefault(false)
                    if (hasImage) {
                        existing.disposable.dispose()
                        states[index] = PreloadState.Success
                        activeIndices.remove(index)
                    } else {
                        existing.disposable.dispose()
                        enqueueIndex(
                            context = context,
                            index = index,
                            url = items[index],
                            imageLoader = imageLoader,
                            sizeResolver = sizeResolver,
                        )
                    }
                }

                PreloadState.Idle -> enqueueIndex(
                    context = context,
                    index = index,
                    url = items[index],
                    imageLoader = imageLoader,
                    sizeResolver = sizeResolver,
                )
            }
        }
    }

    fun disposeUndesired(desiredIndices: IntRange) {
        val toRemove = activeIndices.toSet().filter { it !in desiredIndices }
        for (index in toRemove) {
            val value = states[index]
            if (value is PreloadState.Loading) {
                value.disposable.dispose()
                states[index] = PreloadState.Idle
            }
        }
        activeIndices.retainAll { it in desiredIndices }
    }

    private fun enqueueIndex(
        context: PlatformContext,
        index: Int,
        url: String,
        imageLoader: ImageLoader,
        sizeResolver: SizeResolver,
    ) {
        val request = buildPreloadRequest(
            context = context,
            url = url,
            sizeResolver = sizeResolver,
        )
        states[index] = PreloadState.Loading(imageLoader.enqueue(request))
        activeIndices.add(index)
    }
}

private fun computeWindow(
    firstVisibleItemIndex: Int,
    visibleItemsCount: Int,
    lastIndex: Int,
    preloadCount: Int,
): IntRange {
    val startIndex = (firstVisibleItemIndex - preloadCount).coerceIn(0, lastIndex)
    val endIndex = (firstVisibleItemIndex + visibleItemsCount + preloadCount - 1).coerceIn(startIndex, lastIndex)

    return startIndex..endIndex
}

private fun buildPreloadRequest(context: PlatformContext, url: String, sizeResolver: SizeResolver): ImageRequest =
    ImageRequest.Builder(context).apply {
        data(url)
        size(sizeResolver)
        precision(Precision.INEXACT)
    }.build()

private sealed interface PreloadState {
    data object Idle : PreloadState
    data class Loading(val disposable: Disposable) : PreloadState
    data object Success : PreloadState
}
