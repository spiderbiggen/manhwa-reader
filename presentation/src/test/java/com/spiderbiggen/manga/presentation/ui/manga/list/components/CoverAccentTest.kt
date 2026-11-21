package com.spiderbiggen.manga.presentation.ui.manga.list.components

import androidx.compose.ui.graphics.Color
import com.spiderbiggen.manga.presentation.theme.lightness
import org.junit.Assert.assertEquals
import org.junit.Test

class CoverAccentTest {

    @Test
    fun `given no dominant color when computing the accent then the fallback is used`() {
        val fallback = Color(0xFF123456)

        assertEquals(fallback, coverAccentColor(dominantColor = null, fallback = fallback))
    }

    @Test
    fun `given a dark dominant color when computing the accent then it is kept and made opaque`() {
        val accent = coverAccentColor(dominantColor = 0x00202830, fallback = Color.Black)

        assertEquals(Color(0xFF202830), accent)
    }

    @Test
    fun `given a light dominant color when computing the accent then lightness is capped`() {
        val accent = coverAccentColor(dominantColor = 0x00F8D890, fallback = Color.Black)

        assertEquals(MAX_ACCENT_LIGHTNESS_FOR_WHITE_TEXT, accent.lightness, 0.001f)
        assertEquals(1f, accent.alpha, 0.01f)
    }

    @Test
    fun `given an accent when building the favorite button stops then they fade to transparent`() {
        val accent = Color(0xFF3F6E8C)

        val stops = favoriteButtonColorStops(accent)

        assertEquals(listOf(0f, 0.35f, 0.7f, 1f), stops.map { it.first })
        listOf(0.9f, 0.7f, 0.3f, 0f).zip(stops.map { it.second.alpha }).forEach { (expected, actual)
            ->
            assertEquals(expected, actual, 0.01f)
        }
        assertEquals(Color.Transparent, stops.last().second)
    }
}
