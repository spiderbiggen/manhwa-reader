package com.spiderbiggen.manga.presentation.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.luminance
import kotlin.math.max
import kotlin.math.min
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ThemeTest {

    private val schemes = mapOf(false to lightScheme, true to darkScheme)

    @Test
    fun `given each scheme when text is drawn on its surface then the contrast is readable`() {
        schemes.forEach { (darkTheme, scheme) ->
            with(scheme) {
                assertReadable("text on page, dark=$darkTheme", onSurface, surface)
                assertReadable("muted text on page, dark=$darkTheme", onSurfaceVariant, surface)
                assertReadable("accent on page, dark=$darkTheme", primary, surface)
                assertReadable("accent on rows, dark=$darkTheme", primary, surfaceContainer)
                assertReadable("accent on sheet, dark=$darkTheme", primary, surfaceContainerLow)
                assertReadable("checkmark, dark=$darkTheme", onPrimary, primary)
                assertReadable(
                    "button label, dark=$darkTheme",
                    onPrimaryContainer,
                    primaryContainer,
                )
                assertReadable("snackbar action, dark=$darkTheme", inversePrimary, inverseSurface)
            }
        }
    }

    @Test
    fun `given each scheme when a row is read then its dimmed text stays readable`() {
        schemes.forEach { (darkTheme, scheme) ->
            val dimmed =
                scheme.onSurface
                    .copy(alpha = readContentAlpha(darkTheme))
                    .compositeOver(scheme.surfaceContainer)

            assertReadable("read row, dark=$darkTheme", dimmed, scheme.surfaceContainer)
        }
    }

    @Test
    fun `given both schemes when a button is filled then it has the same colours`() {
        assertEquals(lightScheme.primaryContainer, darkScheme.primaryContainer)
        assertEquals(lightScheme.onPrimaryContainer, darkScheme.onPrimaryContainer)
    }

    private fun assertReadable(pair: String, foreground: Color, background: Color) {
        val first = foreground.luminance() + 0.05f
        val second = background.luminance() + 0.05f
        val ratio = max(first, second) / min(first, second)

        assertTrue("$pair has contrast $ratio", ratio >= MINIMUM_TEXT_CONTRAST)
    }
}

private const val MINIMUM_TEXT_CONTRAST = 4.5f
