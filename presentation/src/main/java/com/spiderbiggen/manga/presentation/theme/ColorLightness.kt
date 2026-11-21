package com.spiderbiggen.manga.presentation.theme

import androidx.compose.ui.graphics.Color
import kotlin.math.abs

/**
 * Returns this colour with its HSL lightness capped at [maxLightness], keeping hue, saturation and
 * alpha. Colours at or below the cap are returned unchanged, so dark colours are never dimmed
 * further.
 */
fun Color.coerceLightnessAtMost(maxLightness: Float): Color =
    if (lightness <= maxLightness) this else withLightness(maxLightness)

/** HSL lightness: the midpoint of the largest and smallest RGB channel. */
val Color.lightness: Float
    get() = (maxOf(red, green, blue) + minOf(red, green, blue)) / 2f

private fun Color.withLightness(target: Float): Color {
    val max = maxOf(red, green, blue)
    val min = minOf(red, green, blue)
    val delta = max - min
    if (delta == 0f) return Color(target, target, target, alpha)

    val saturation = delta / (1f - abs(2f * lightness - 1f))
    val hue =
        60f *
            when (max) {
                red -> ((green - blue) / delta).mod(6f)
                green -> (blue - red) / delta + 2f
                else -> (red - green) / delta + 4f
            }

    val chroma = (1f - abs(2f * target - 1f)) * saturation
    val secondary = chroma * (1f - abs((hue / 60f).mod(2f) - 1f))
    val offset = target - chroma / 2f
    val (r, g, b) =
        when (hue.toInt() / 60) {
            0 -> Triple(chroma, secondary, 0f)
            1 -> Triple(secondary, chroma, 0f)
            2 -> Triple(0f, chroma, secondary)
            3 -> Triple(0f, secondary, chroma)
            4 -> Triple(secondary, 0f, chroma)
            else -> Triple(chroma, 0f, secondary)
        }
    return Color(
        (r + offset).coerceIn(0f, 1f),
        (g + offset).coerceIn(0f, 1f),
        (b + offset).coerceIn(0f, 1f),
        alpha,
    )
}
