package com.spiderbiggen.manga.presentation.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun Modifier.scrollableFade(
    canScrollBackward: () -> Boolean,
    canScrollForward: () -> Boolean,
    topFade: Dp = 32.dp,
    bottomFade: Dp = 24.dp,
): Modifier = composed {
    val fadeTop = canScrollBackward()
    val fadeBottom = canScrollForward()
    val topFadeSize by animateDpAsState(if (fadeTop) topFade else 0.dp)
    val bottomFadeSize by animateDpAsState(if (fadeBottom) bottomFade else 0.dp)
    graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
        .drawWithContent {
            drawContent()
            val topFadeSizePx = topFadeSize.toPx()
            drawRect(
                size = size.copy(height = topFadeSizePx),
                brush = Brush.verticalGradient(
                    colors = listOf(Color.Transparent, Color.Black),
                    startY = 0f,
                    endY = topFadeSizePx,
                ),
                blendMode = BlendMode.DstIn,
            )
            val bottomFadeSizePx = bottomFadeSize.toPx()
            drawRect(
                topLeft = Offset(0f, size.height - bottomFadeSizePx),
                size = size.copy(height = bottomFadeSizePx),
                brush = Brush.verticalGradient(
                    colors = listOf(Color.Black, Color.Transparent),
                    startY = size.height - bottomFadeSizePx,
                    endY = size.height,
                ),
                blendMode = BlendMode.DstIn,
            )
        }
}
