package com.spiderbiggen.manga.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.spiderbiggen.manga.presentation.R
import com.spiderbiggen.manga.presentation.theme.MangaReaderTheme

/** The ink drop logo. Its folded corner is dark or light, following the system night mode. */
@Composable
fun InkyLogo(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(R.drawable.logo_inky),
        contentDescription = stringResource(R.string.logo_description),
        modifier = modifier,
    )
}

@PreviewLightDark
@Composable
private fun InkyLogoPreview() {
    MangaReaderTheme {
        Surface {
            InkyLogo()
        }
    }
}
