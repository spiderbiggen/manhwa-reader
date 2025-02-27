package com.spiderbiggen.manga.presentation.components.topappbar

import androidx.compose.animation.core.Animatable
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
fun rememberTopAppBarState(initialHeight: Float = 0f): TopAppBarState {
    return rememberSaveable(saver = TopAppBarState.Saver) {
        TopAppBarState(initialHeight)
    }
}

class TopAppBarState {
    constructor(topAppBarHeight: Float) : this(topAppBarHeight, 0f)
    private constructor(height: Float, offset: Float) {
        mutableOffset = mutableFloatStateOf(offset)
        mutableHeight = height
    }

    private var mutableHeight: Float
    var appBarHeight: Float
        get() = mutableHeight
        set(value) {
            if (mutableOffset.floatValue < -value) {
                mutableOffset.floatValue = -value
            }

            mutableHeight = value
        }

    private var animationJob: Job? = null

    private val mutableOffset: MutableFloatState
    val appBarOffset: FloatState
        get() = mutableOffset.asFloatState()

    suspend fun animateAppBarOffset(offset: Float) {
        val limited = offset.coerceIn(-appBarHeight, 0f)
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
            animationJob?.cancel()
            mutableOffset.floatValue = (mutableOffset.floatValue + delta)
                .coerceIn(-appBarHeight, 0f)
            return Offset.Zero
        }
    }

    companion object {
        /**
         * The default [Saver] implementation for [TopAppBarState].
         */
        val Saver: Saver<TopAppBarState, *> = listSaver(
            save = { listOf(it.appBarHeight, it.mutableOffset.floatValue) },
            restore = {
                TopAppBarState(
                    height = it[0],
                    offset = it[1],
                )
            },
        )
    }
}
