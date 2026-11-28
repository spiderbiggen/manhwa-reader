package com.spiderbiggen.manga.presentation.components.input

import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

/**
 * Colours for filled buttons. They keep the deep accent with white content in both themes. Material
 * would use the primary colour, which is lightened in the dark theme to stay readable as text.
 */
object AccentButtonDefaults {
    @Composable
    fun buttonColors(): ButtonColors =
        ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        )

    @Composable
    fun filledIconButtonColors(): IconButtonColors =
        IconButtonDefaults.filledIconButtonColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        )
}
