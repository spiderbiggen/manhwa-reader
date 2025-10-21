package com.spiderbiggen.manga.presentation.components.topappbar

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.spring
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
import androidx.compose.ui.util.fastCoerceIn
import com.spiderbiggen.manga.presentation.components.topappbar.TopAppBarState.Companion.Saver
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@Composable
fun rememberTopAppBarState(initialHeight: Float = 0f): TopAppBarState {
    return rememberSaveable(saver = Saver) {
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

    suspend fun animateAppBarOffset(offset: Float, animationSpec: AnimationSpec<Float> = spring()) {
        val limited = offset.fastCoerceIn(-appBarHeight, 0f)
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
            val delta = available.y
            val startOffset = mutableOffset.floatValue
            val newOffset = (startOffset + delta).fastCoerceIn(-appBarHeight, 0f)
            if (newOffset == startOffset) return Offset.Zero
            mutableOffset.floatValue = newOffset
            val consumedDelta = newOffset - startOffset
            println("TopAppBarState.onPreScroll, available: $available, consumed: $consumedDelta")
            return Offset(0f, consumedDelta)
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
