package com.spiderbiggen.manga.presentation.theme

import androidx.compose.ui.graphics.Color
import org.junit.Assert.assertEquals
import org.junit.Test

class ColorLightnessTest {

    @Test
    fun `given color below the cap when capping lightness then color is unchanged`() {
        val color = Color(0xFF202830)

        assertEquals(color, color.coerceLightnessAtMost(0.45f))
    }

    @Test
    fun `given color above the cap when capping lightness then lightness equals the cap`() {
        val capped = Color(0xFFF8D890).coerceLightnessAtMost(0.45f)

        assertEquals(0.45f, capped.lightness, 0.001f)
    }

    @Test
    fun `given color above the cap when capping lightness then hue is preserved`() {
        val original = Color(0xFF3399FF)
        val capped = original.coerceLightnessAtMost(0.3f)

        // Hue is preserved when the ratios between the channel differences stay the same.
        val originalRatio = (original.green - original.red) / (original.blue - original.red)
        val cappedRatio = (capped.green - capped.red) / (capped.blue - capped.red)
        assertEquals(originalRatio, cappedRatio, 0.01f)
    }

    @Test
    fun `given grey above the cap when capping lightness then it becomes the capped grey`() {
        val capped = Color(0.9f, 0.9f, 0.9f).coerceLightnessAtMost(0.4f)

        assertEquals(Color(0.4f, 0.4f, 0.4f), capped)
    }

    @Test
    fun `given translucent color above the cap when capping lightness then alpha is preserved`() {
        val capped = Color(1f, 1f, 1f, 0.5f).coerceLightnessAtMost(0.4f)

        assertEquals(0.5f, capped.alpha, 0.01f)
    }
}
