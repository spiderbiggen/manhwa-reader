package com.spiderbiggen.manga.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.materialkolor.DynamicMaterialTheme
import com.materialkolor.PaletteStyle

@Composable
fun MangaReaderTheme(
    seedColor: Color = Purple80,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    DynamicMaterialTheme(
        seedColor = seedColor,
        useDarkTheme = darkTheme,
        style = PaletteStyle.Expressive,
        content = content,
    )
}
