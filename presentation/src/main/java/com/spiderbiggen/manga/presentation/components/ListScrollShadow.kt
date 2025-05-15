package com.spiderbiggen.manga.presentation.components

import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun Modifier.scrollableFade(
    canScrollBackward: () -> Boolean,
    canScrollForward: () -> Boolean,
    fadeSize: Dp = 64.dp,
): Modifier = composed {
    val fadeTop = canScrollBackward()
    val fadeBottom = canScrollForward()
    val fadeSize = with(LocalDensity.current) {
        fadeSize.toPx()
    }
    val topFadeSizePx by animateFloatAsState(if (fadeTop) fadeSize else 0f)
    val bottomFadeSizePx by animateFloatAsState(if (fadeBottom) fadeSize else 0f)
    graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
        .drawWithContent {
            drawContent()
            drawRect(
                size = size.copy(height = topFadeSizePx),
                brush = Brush.verticalGradient(
                    colors = listOf(Color.Transparent, Color.Black),
                    startY = 0f,
                    endY = topFadeSizePx,
                ),
                blendMode = BlendMode.DstIn,
            )
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
