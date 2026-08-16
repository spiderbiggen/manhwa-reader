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

@Composable
@CoverageExcluded
fun StatusBarProtection(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.background,
    heightProvider: () -> Float = calculateStatusBarGradientHeight(),
) {
    Canvas(modifier.fillMaxSize()) {
        val calculatedHeight = heightProvider()
        val gradient =
            Brush.verticalGradient(
                colors =
                    listOf(
                        color.copy(alpha = 1f),
                        Color.Transparent,
                    ),
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
    return { statusBars.getTop(density).toFloat() }
}
