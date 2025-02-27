package com.spiderbiggen.manga.presentation.components.bottomappbar


import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.FloatState
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.asFloatState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@Composable
fun rememberBottomAppBarState(lazyListState: LazyListState, initialHeight: Float = 0f): BottomAppBarState {
    return rememberSaveable(lazyListState, saver = BottomAppBarState.saver(lazyListState)) {
        BottomAppBarState(lazyListState, initialHeight)
    }
}

class BottomAppBarState {
    constructor(lazyListState: LazyListState, bottomAppBarHeight: Float) : this(lazyListState, bottomAppBarHeight, 0f)
    private constructor(lazyListState: LazyListState, height: Float, offset: Float) {
        this.mutableOffset = mutableFloatStateOf(offset)
        this.mutableHeight = height
        this.lazyListState = lazyListState
    }

    private val lazyListState: LazyListState
    private var mutableHeight: Float
    var appBarHeight: Float
        get() = mutableHeight
        set(value) {
            if (mutableOffset.floatValue > value) {
                mutableOffset.floatValue = value
            }

            mutableHeight = value
        }

    private var animationJob: Job? = null

    private val mutableOffset: MutableFloatState
    val appBarOffset: FloatState
        get() = mutableOffset.asFloatState()

    suspend fun animateAppBarOffset(offset: Float) {
        val limited = offset.coerceIn(0f, appBarHeight)
        if (mutableOffset.floatValue == limited) return
        animationJob = coroutineScope {
            launch {
                Animatable(mutableOffset.floatValue)
                    .animateTo(targetValue = offset) { mutableOffset.floatValue = value }
            }
        }
    }

    val nestedScrollConnection = object : NestedScrollConnection {
        override fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource): Offset {
            val delta = consumed.y

            val maxBottomOffsetValue = appBarHeight - getBottomPadding(lazyListState, appBarHeight)
            mutableOffset.floatValue = (mutableOffset.floatValue - delta)
                .coerceIn(0f, maxBottomOffsetValue.coerceAtLeast(0f))
            return Offset.Zero
        }

        private fun getBottomPadding(lazyListState: LazyListState, maxBottomOffSet: Float): Float {
            val info = lazyListState.layoutInfo
            val lastVisibleItem = info.visibleItemsInfo.lastOrNull() ?: return maxBottomOffSet

            val count = info.totalItemsCount
            if (lastVisibleItem.index + 2 < count) return 0f

            val consumedSize = lastVisibleItem.offset + lastVisibleItem.size + info.afterContentPadding
            val bottomOverflow = (consumedSize - info.viewportEndOffset)
            val bottomPadding = (maxBottomOffSet - bottomOverflow).coerceAtLeast(0f)
            return bottomPadding
        }
    }

    companion object {
        internal fun saver(lazyListState: LazyListState): Saver<BottomAppBarState, *> =
            listSaver(
                save = { listOf(it.appBarHeight, it.mutableOffset.floatValue) },
                restore = {
                    BottomAppBarState(
                        lazyListState = lazyListState,
                        height = it[0],
                        offset = it[1],
                    )
                }
            )
    }
}
