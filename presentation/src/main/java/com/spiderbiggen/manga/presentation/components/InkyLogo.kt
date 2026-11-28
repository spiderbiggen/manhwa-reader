package com.spiderbiggen.manga.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.spiderbiggen.manga.presentation.R
import com.spiderbiggen.manga.presentation.theme.MangaReaderTheme

/** The ink drop logo. Its folded corner takes the content colour. */
@Composable
fun InkyLogo(modifier: Modifier = Modifier) {
    Box(modifier) {
        Image(
            painter = painterResource(R.drawable.logo_inky_drop),
            contentDescription = stringResource(R.string.logo_description),
        )
        Icon(
            painter = painterResource(R.drawable.logo_inky_fold),
            contentDescription = null,
        )
    }
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
