package com.spiderbiggen.manga.presentation.theme

import androidx.compose.ui.graphics.Color
import kotlin.math.abs
import kotlin.math.min

/**
 * Returns this colour with its HSL lightness capped at [maxLightness], keeping hue, saturation and
 * alpha. Colours at or below the cap are returned unchanged, so dark colours are never dimmed
 * further.
 */
internal fun Color.coerceLightnessAtMost(maxLightness: Float): Color =
    if (lightness <= maxLightness) this else withLightness(maxLightness)

/** HSL lightness: the midpoint of the largest and smallest RGB channel. */
internal val Color.lightness: Float
    get() = (maxOf(red, green, blue) + minOf(red, green, blue)) / 2f

private fun Color.withLightness(target: Float): Color {
    val max = maxOf(red, green, blue)
    val delta = max - minOf(red, green, blue)
    if (delta == 0f) return Color(target, target, target, alpha)

    val saturation = delta / (1f - abs(2f * lightness - 1f))
    val sector =
        when (max) {
            red -> ((green - blue) / delta).mod(6f)
            green -> (blue - red) / delta + 2f
            else -> (red - green) / delta + 4f
        }
    val hue = 60f * sector

    // HSL to RGB: channel(n) = L - a * clamp(min(k - 3, 9 - k), -1, 1) with k = (n + H / 30) mod
    // 12.
    val amplitude = saturation * min(target, 1f - target)

    fun channel(n: Int): Float {
        val k = (n + hue / 30f).mod(12f)
        return target - amplitude * min(k - 3f, 9f - k).coerceIn(-1f, 1f)
    }
    return Color(channel(0), channel(8), channel(4), alpha)
}
