package com.spiderbiggen.manga.presentation.theme

import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.spiderbiggen.manga.presentation.R

val displayFontFamily = FontFamily(
    Font(R.font.fredoka_bold, weight = FontWeight.Bold),
    Font(R.font.fredoka_medium, weight = FontWeight.Medium),
    Font(R.font.fredoka_regular, weight = FontWeight.Normal),
)
val bodyFontFamily = displayFontFamily

// Default Material 3 typography values
// Baseline uses the old constructor :( so the expressive variants are not available
val baseline = Typography()

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
val AppTypography = Typography(
    displayLarge = baseline.displayLarge.copy(
        fontFamily = displayFontFamily,
    ),
    displayMedium = baseline.displayMedium.copy(
        fontFamily = displayFontFamily,
    ),
    displaySmall = baseline.displaySmall.copy(
        fontFamily = displayFontFamily,
    ),
    headlineLarge = baseline.headlineLarge.copy(
        fontFamily = displayFontFamily,
    ),
    headlineMedium = baseline.headlineMedium.copy(
        fontFamily = displayFontFamily,
    ),
    headlineSmall = baseline.headlineSmall.copy(
        fontFamily = displayFontFamily,
    ),
    titleLarge = baseline.titleLarge.copy(
        fontFamily = displayFontFamily,
    ),
    titleMedium = baseline.titleMedium.copy(
        fontFamily = displayFontFamily,
    ),
    titleSmall = baseline.titleSmall.copy(
        fontFamily = displayFontFamily,
    ),
    bodyLarge = baseline.bodyLarge.copy(
        fontFamily = bodyFontFamily,
    ),
    bodyMedium = baseline.bodyMedium.copy(
        fontFamily = bodyFontFamily,
    ),
    bodySmall = baseline.bodySmall.copy(
        fontFamily = bodyFontFamily,
    ),
    labelLarge = baseline.labelLarge.copy(
        fontFamily = bodyFontFamily,
    ),
    labelMedium = baseline.labelMedium.copy(
        fontFamily = bodyFontFamily,
    ),
    labelSmall = baseline.labelSmall.copy(
        fontFamily = bodyFontFamily,
    ),
    // Emphasized
    displayLargeEmphasized = baseline.displayLargeEmphasized.copy(
        fontFamily = displayFontFamily,
        fontWeight = FontWeight.Medium,
    ),
    displayMediumEmphasized = baseline.displayMediumEmphasized.copy(
        fontFamily = displayFontFamily,
        fontWeight = FontWeight.Medium,
    ),
    displaySmallEmphasized = baseline.displaySmallEmphasized.copy(
        fontFamily = displayFontFamily,
        fontWeight = FontWeight.Medium,
    ),
    headlineLargeEmphasized = baseline.headlineLargeEmphasized.copy(
        fontFamily = displayFontFamily,
        fontWeight = FontWeight.Medium,
    ),
    headlineMediumEmphasized = baseline.headlineMediumEmphasized.copy(
        fontFamily = displayFontFamily,
        fontWeight = FontWeight.Medium,
    ),
    headlineSmallEmphasized = baseline.headlineSmallEmphasized.copy(
        fontFamily = displayFontFamily,
        fontWeight = FontWeight.Medium,
    ),
    titleLargeEmphasized = baseline.titleLargeEmphasized.copy(
        fontFamily = displayFontFamily,
        fontWeight = FontWeight.Medium,
    ),
    titleMediumEmphasized = baseline.titleMediumEmphasized.copy(
        fontFamily = displayFontFamily,
        fontWeight = FontWeight.Bold,
    ),
    titleSmallEmphasized = baseline.titleSmallEmphasized.copy(
        fontFamily = displayFontFamily,
        fontWeight = FontWeight.Bold,
    ),
    bodyLargeEmphasized = baseline.bodyLargeEmphasized.copy(
        fontFamily = bodyFontFamily,
        fontWeight = FontWeight.Medium,
    ),
    bodyMediumEmphasized = baseline.bodyMediumEmphasized.copy(
        fontFamily = bodyFontFamily,
        fontWeight = FontWeight.Medium,
    ),
    bodySmallEmphasized = baseline.bodySmallEmphasized.copy(
        fontFamily = bodyFontFamily,
        fontWeight = FontWeight.Medium,
    ),
    labelLargeEmphasized = baseline.labelLargeEmphasized.copy(
        fontFamily = bodyFontFamily,
        fontWeight = FontWeight.Bold,
    ),
    labelMediumEmphasized = baseline.labelMediumEmphasized.copy(
        fontFamily = bodyFontFamily,
        fontWeight = FontWeight.Bold,
    ),
    labelSmallEmphasized = baseline.labelSmallEmphasized.copy(
        fontFamily = bodyFontFamily,
        fontWeight = FontWeight.Bold,
    ),
)
