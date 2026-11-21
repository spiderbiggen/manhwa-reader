package com.spiderbiggen.manga.presentation.ui.manga.list.components

import androidx.compose.ui.graphics.Color
import com.spiderbiggen.manga.presentation.theme.coerceLightnessAtMost

// Keeps the white title text legible; colors at or below the cap are left untouched.
internal const val MAX_ACCENT_LIGHTNESS_FOR_WHITE_TEXT = 0.45f

internal fun coverAccentColor(dominantColor: Int?, fallback: Color): Color =
    dominantColor?.let {
        Color(it).copy(alpha = 1f).coerceLightnessAtMost(MAX_ACCENT_LIGHTNESS_FOR_WHITE_TEXT)
    } ?: fallback

internal fun favoriteButtonColorStops(accentColor: Color): List<Pair<Float, Color>> =
    listOf(
        0f to accentColor.copy(alpha = 0.9f),
        0.35f to accentColor.copy(alpha = 0.7f),
        0.7f to accentColor.copy(alpha = 0.3f),
        1f to Color.Transparent,
    )
