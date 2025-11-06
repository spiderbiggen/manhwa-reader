package com.spiderbiggen.manga.presentation.theme

import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight

import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.text.googlefonts.Font
import com.spiderbiggen.manga.presentation.R

val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

val bodyFontFamily = FontFamily(
    Font(
        googleFont = GoogleFont("Roboto"),
        fontProvider = provider,
    ),
    Font(resId = R.font.roboto, style = FontStyle.Italic),
    Font(resId = R.font.roboto),
)

val displayFontFamily = FontFamily(
    Font(
        googleFont = GoogleFont("Fredoka"),
        fontProvider = provider,
    ),
    Font(resId = R.font.fredoka),
)

// Default Material 3 typography values
val baseline = Typography()

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
val AppTypography = Typography(
    displayLarge = baseline.displayLarge.copy(fontFamily = displayFontFamily),
    displayMedium = baseline.displayMedium.copy(fontFamily = displayFontFamily),
    displaySmall = baseline.displaySmall.copy(fontFamily = displayFontFamily),
    headlineLarge = baseline.headlineLarge.copy(fontFamily = displayFontFamily),
    headlineMedium = baseline.headlineMedium.copy(fontFamily = displayFontFamily),
    headlineSmall = baseline.headlineSmall.copy(fontFamily = displayFontFamily),
    titleLarge = baseline.titleLarge.copy(fontFamily = displayFontFamily),
    titleMedium = baseline.titleMedium.copy(fontFamily = displayFontFamily),
    titleSmall = baseline.titleSmall.copy(fontFamily = displayFontFamily),
    bodyLarge = baseline.bodyLarge.copy(fontFamily = bodyFontFamily),
    bodyMedium = baseline.bodyMedium.copy(fontFamily = bodyFontFamily),
    bodySmall = baseline.bodySmall.copy(fontFamily = bodyFontFamily),
    labelLarge = baseline.labelLarge.copy(fontFamily = bodyFontFamily),
    labelMedium = baseline.labelMedium.copy(fontFamily = bodyFontFamily),
    labelSmall = baseline.labelSmall.copy(fontFamily = bodyFontFamily),
    // Emphasized
    displayLargeEmphasized = baseline.displayLarge.copy(fontFamily = displayFontFamily, fontWeight = FontWeight.Medium),
    displayMediumEmphasized = baseline.displayMedium.copy(fontFamily = displayFontFamily, fontWeight = FontWeight.Medium),
    displaySmallEmphasized = baseline.displaySmall.copy(fontFamily = displayFontFamily, fontWeight = FontWeight.Medium),
    headlineLargeEmphasized = baseline.headlineLarge.copy(fontFamily = displayFontFamily, fontWeight = FontWeight.Medium),
    headlineMediumEmphasized = baseline.headlineMedium.copy(fontFamily = displayFontFamily, fontWeight = FontWeight.Medium),
    headlineSmallEmphasized = baseline.headlineSmall.copy(fontFamily = displayFontFamily, fontWeight = FontWeight.Medium),
    titleLargeEmphasized = baseline.titleLarge.copy(fontFamily = displayFontFamily, fontWeight = FontWeight.Medium),
    titleMediumEmphasized = baseline.titleMedium.copy(fontFamily = displayFontFamily, fontWeight = FontWeight.Bold),
    titleSmallEmphasized = baseline.titleSmall.copy(fontFamily = displayFontFamily, fontWeight = FontWeight.Bold),
    bodyLargeEmphasized = baseline.bodyLarge.copy(fontFamily = bodyFontFamily, fontWeight = FontWeight.Medium),
    bodyMediumEmphasized = baseline.bodyMedium.copy(fontFamily = bodyFontFamily, fontWeight = FontWeight.Medium),
    bodySmallEmphasized = baseline.bodySmall.copy(fontFamily = bodyFontFamily, fontWeight = FontWeight.Medium),
    labelLargeEmphasized = baseline.labelLarge.copy(fontFamily = bodyFontFamily, fontWeight = FontWeight.Bold),
    labelMediumEmphasized = baseline.labelMedium.copy(fontFamily = bodyFontFamily, fontWeight = FontWeight.Bold),
    labelSmallEmphasized = baseline.labelSmall.copy(fontFamily = bodyFontFamily, fontWeight = FontWeight.Bold),
)

