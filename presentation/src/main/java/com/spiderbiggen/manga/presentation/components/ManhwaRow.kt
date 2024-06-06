package com.spiderbiggen.manga.presentation.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.dropUnlessResumed
import coil.compose.AsyncImage
import com.spiderbiggen.manga.domain.model.id.MangaId
import com.spiderbiggen.manga.presentation.theme.MangaReaderTheme
import com.spiderbiggen.manga.presentation.ui.manga.model.MangaViewData

private const val ASPECT_RATIO = 1.35F
private val aspectModifier = Modifier
    .height(96.dp)
    .width(96.dp / ASPECT_RATIO)

@Composable
fun MangaRow(
    manga: MangaViewData,
    navigateToManga: (MangaId) -> Unit,
    onClickFavorite: (MangaId) -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surface,
) {
    Surface(
        onClick = dropUnlessResumed { navigateToManga(manga.id) },
        modifier = modifier.fillMaxWidth(),
        color = containerColor,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            AsyncImage(
                model = manga.coverImage,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = aspectModifier,
                alignment = Alignment.Center,
            )
            MangaInfoColumn(manga, Modifier.weight(1f))
            if (manga.status == "Dropped") {
                Icon(
                    Icons.Rounded.Warning,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                )
            }
            FavoriteButton(manga.id, manga.isFavorite, onClickFavorite)
        }
    }
}

@Composable
private fun MangaInfoColumn(manga: MangaViewData, modifier: Modifier) {
    Column(
        modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically),
    ) {
        val contentColor = LocalContentColor.current.let {
            if (manga.readAll) it.copy(alpha = 0.7f) else it
        }
        CompositionLocalProvider(LocalContentColor provides contentColor) {
            Text(
                manga.title,
                fontWeight = if (!manga.readAll) FontWeight.Bold else null,
                style = MaterialTheme.typography.bodyLarge,
            )
            manga.updatedAt?.let {
                Text(
                    it,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }
}

@Composable
private fun FavoriteButton(mangaId: MangaId, isFavorite: Boolean, onClickFavorite: (MangaId) -> Unit) {
    IconButton(
        onClick = dropUnlessResumed { onClickFavorite(mangaId) },
        modifier = Modifier.padding(end = 16.dp),
    ) {
        Icon(
            if (isFavorite) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
            contentDescription = null,
        )
    }
}

@Preview("Light")
@Preview("Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewManga(@PreviewParameter(MangaViewDataProvider::class) state: MangaViewData) {
    MangaReaderTheme {
        MangaRow(state, {}, {})
    }
}

class MangaViewDataProvider : PreviewParameterProvider<MangaViewData> {
    override val values
        get() = sequenceOf(
            MangaViewData(
                source = "Asura",
                id = MangaId("712dd47d646544338484357604d6cf80"),
                title = "Heavenly Martial God",
                coverImage = "https://www.asurascans.com/wp-content/uploads/2021/09/martialgod.jpg",
                status = "Ongoing",
                updatedAt = "2023-04-23",
                isFavorite = false,
                readAll = false,
            ),
            MangaViewData(
                source = "Asura",
                id = MangaId("712dd47d646544338484357604d6cf81"),
                title = "Heavenly Martial God",
                coverImage = "https://www.asurascans.com/wp-content/uploads/2021/09/martialgod.jpg",
                status = "Ongoing",
                updatedAt = "2023-04-23",
                isFavorite = true,
                readAll = true,
            ),
        )
}
