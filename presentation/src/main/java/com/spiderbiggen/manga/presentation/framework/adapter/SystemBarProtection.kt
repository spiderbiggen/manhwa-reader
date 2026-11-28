package com.spiderbiggen.manga.presentation.framework.adapter

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import com.spiderbiggen.manga.presentation.coverage.CoverageExcluded

/** How far the protection reaches, relative to the height of the status bar. */
private const val GRADIENT_HEIGHT_SCALE = 1.25f

/**
 * Alpha at evenly spaced points from top to bottom, following `1 - smoothstep(position ^ 1.6)`. The
 * small steps avoid sharp changes in slope, which can show up as a visible line.
 */
private val gradientAlphas =
    floatArrayOf(1f, 1f, 0.98f, 0.94f, 0.86f, 0.75f, 0.59f, 0.4f, 0.22f, 0.07f, 0f)

@Composable
@CoverageExcluded
fun StatusBarProtection(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.background,
    heightProvider: () -> Float = calculateStatusBarGradientHeight(),
) {
    Canvas(modifier.fillMaxSize()) {
        val calculatedHeight = heightProvider()
        val colorStops =
            Array(gradientAlphas.size) { index ->
                index / gradientAlphas.lastIndex.toFloat() to
                    color.copy(alpha = gradientAlphas[index])
            }
        val gradient =
            Brush.verticalGradient(
                colorStops = colorStops,
                startY = 0f,
                endY = calculatedHeight,
            )
        drawRect(
            brush = gradient,
            size = Size(size.width, calculatedHeight),
        )
    }
}

@Composable
private fun calculateStatusBarGradientHeight(): () -> Float {
    val statusBars = WindowInsets.statusBars
    val density = LocalDensity.current
    return { statusBars.getTop(density) * GRADIENT_HEIGHT_SCALE }
}
