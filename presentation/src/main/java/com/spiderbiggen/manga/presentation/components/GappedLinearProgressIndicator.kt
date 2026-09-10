package com.spiderbiggen.manga.presentation.components

import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.layout.layout
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.offset
import androidx.compose.ui.util.fastCoerceIn
import kotlin.math.abs
import kotlin.math.min

@Composable
fun GappedLinearProgressIndicator(
    progress: () -> Float,
    modifier: Modifier = Modifier,
    color: Color = ProgressIndicatorDefaults.linearColor,
    trackColor: Color = ProgressIndicatorDefaults.linearTrackColor,
    strokeCap: StrokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
    gapSize: Dp = ProgressIndicatorDefaults.LinearIndicatorTrackGapSize,
    stopSize: Dp = ProgressIndicatorDefaults.LinearTrackStopIndicatorSize,
    drawStopIndicator: DrawScope.() -> Unit = {
        ProgressIndicatorDefaults.drawStopIndicator(
            drawScope = this,
            stopSize = ProgressIndicatorDefaults.LinearTrackStopIndicatorSize,
            color = color,
            strokeCap = strokeCap,
        )
    },
) {
    val coercedProgress = { progress().fastCoerceIn(0f, 1f) }
    Canvas(
        modifier
            .then(IncreaseVerticalSemanticsBounds)
            .semantics(mergeDescendants = true) {
                // Check for NaN, as the ProgressBarRangeInfo will throw an exception.
                progressBarRangeInfo =
                    ProgressBarRangeInfo(coercedProgress().takeUnless { it.isNaN() } ?: 0f, 0f..1f)
            }
            .size(LinearIndicatorWidth, LinearIndicatorHeight)
    ) {
        val strokeWidth = size.height
        val adjustedGapSize =
            if (strokeCap == StrokeCap.Butt || size.height > size.width) {
                gapSize
            } else {
                gapSize + strokeWidth.toDp()
            }
        val widthDp = size.width.toDp()
        val gapSizeFraction = adjustedGapSize / widthDp
        val currentCoercedProgress = coercedProgress()

        // track
        val trackStartFraction =
            currentCoercedProgress + min(currentCoercedProgress, gapSizeFraction)
        if (trackStartFraction <= 1f) {
            drawLinearIndicator(trackStartFraction, 1f, trackColor, strokeWidth, strokeCap)
        }

        // indicator
        if (currentCoercedProgress < 1f) {
            val stopIndicatorFraction = stopSize / widthDp
            val endFraction =
                currentCoercedProgress.fastCoerceIn(
                    0f,
                    1f - gapSizeFraction - stopIndicatorFraction,
                )
            drawLinearIndicator(0f, endFraction, color, strokeWidth, strokeCap)
        } else {
            drawLinearIndicator(0f, 1f, color, strokeWidth, strokeCap)
        }

        // stop
        drawStopIndicator(this)
    }
}

private fun DrawScope.drawLinearIndicator(
    startFraction: Float,
    endFraction: Float,
    color: Color,
    strokeWidth: Float,
    strokeCap: StrokeCap,
) {
    val width = size.width
    val height = size.height
    // Start drawing from the vertical center of the stroke
    val yOffset = height / 2

    val isLtr = layoutDirection == LayoutDirection.Ltr
    val barStart = (if (isLtr) startFraction else 1f - endFraction) * width
    val barEnd = (if (isLtr) endFraction else 1f - startFraction) * width

    // if there isn't enough space to draw the stroke caps, fall back to StrokeCap.Butt
    if (strokeCap == StrokeCap.Butt || height > width) {
        // Progress line
        drawLine(color, Offset(barStart, yOffset), Offset(barEnd, yOffset), strokeWidth)
    } else {
        // need to adjust barStart and barEnd for the stroke caps
        val strokeCapOffset = strokeWidth / 2
        val adjustedBarStart = barStart.fastCoerceIn(strokeCapOffset, width - strokeCapOffset)
        val adjustedBarEnd = barEnd.fastCoerceIn(strokeCapOffset, width - strokeCapOffset)

        if (abs(endFraction - startFraction) > 0) {
            // Progress line
            drawLine(
                color,
                Offset(adjustedBarStart, yOffset),
                Offset(adjustedBarEnd, yOffset),
                strokeWidth,
                strokeCap,
            )
        }
    }
}

internal val LinearIndicatorWidth
    get() = 240.dp

internal val LinearIndicatorHeight
    get() = 4.0.dp

/** Copied from androidx.compose.material3.internal.AccessibilityUtil */
@VisibleForTesting
internal val VerticalSemanticsBoundsPadding: Dp
    get() = 10.dp
internal val IncreaseVerticalSemanticsBounds: Modifier =
    Modifier.layout { measurable, constraints ->
            val paddingPx = VerticalSemanticsBoundsPadding.roundToPx()
            // We need to add vertical padding to the semantics bounds in order to meet
            // screenreader green box minimum size, but we also want to
            // preserve a visual appearance and layout size below that minimum
            // in order to maintain backwards compatibility. This custom
            // layout effectively implements "negative padding".
            val newConstraint = constraints.offset(0, paddingPx * 2)
            val placeable = measurable.measure(newConstraint)

            // But when actually placing the placeable, create the layout without additional
            // space. Place the placeable where it would've been without any extra padding.
            val height = placeable.height - paddingPx * 2
            val width = placeable.width
            layout(width, height) { placeable.place(0, -paddingPx) }
        }
        .semantics(mergeDescendants = true) {}
        .padding(vertical = VerticalSemanticsBoundsPadding)
