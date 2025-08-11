package com.spiderbiggen.manga.presentation.components.bottomappbar


import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.spring
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
import androidx.compose.ui.util.fastCoerceAtLeast
import androidx.compose.ui.util.fastCoerceIn
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

    suspend fun animateAppBarOffset(offset: Float, animationSpec: AnimationSpec<Float> = spring()) {
        val limited = offset.coerceIn(0f, appBarHeight)
        if (mutableOffset.floatValue == limited) return
        animationJob = coroutineScope {
            launch {
                Animatable(mutableOffset.floatValue)
                    .animateTo(targetValue = offset, animationSpec = animationSpec) {
                        mutableOffset.floatValue = value
                    }
            }
        }
    }

    val nestedScrollConnection = object : NestedScrollConnection {
        override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
            animationJob?.cancel()
            val yScroll = available.y

            val lastItemIsVisible = lastItemIsVisible(lazyListState)
            if (lastItemIsVisible && yScroll < 0f) {
                val delta = yScroll.fastCoerceAtLeast(-mutableOffset.floatValue)
                mutableOffset.floatValue = (mutableOffset.floatValue + delta).fastCoerceAtLeast(0f)
            } else {
                mutableOffset.floatValue = (mutableOffset.floatValue - yScroll).fastCoerceIn(0f, appBarHeight)
            }
            return Offset.Zero
        }

        private fun lastItemIsVisible(lazyListState: LazyListState): Boolean {
            val info = lazyListState.layoutInfo
            val lastVisibleItem = info.visibleItemsInfo.lastOrNull() ?: return false

            val count = info.totalItemsCount
            return lastVisibleItem.index + 1 >= count
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
