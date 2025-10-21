package com.spiderbiggen.manga.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MotionScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.materialkolor.DynamicMaterialExpressiveTheme
import com.materialkolor.DynamicMaterialTheme
import com.materialkolor.PaletteStyle
import com.materialkolor.dynamiccolor.ColorSpec

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MangaReaderTheme(
    seedColor: Color = Purple80,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    DynamicMaterialExpressiveTheme(
        seedColor = seedColor,
        motionScheme = MotionScheme.expressive(),
        isDark = darkTheme,
        animate = true,
        content = content,
    )
}
