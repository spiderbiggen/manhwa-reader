package com.spiderbiggen.manga.presentation.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.dropUnlessResumed
import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.compose.AsyncImage
import com.spiderbiggen.manga.domain.model.id.MangaId
import com.spiderbiggen.manga.presentation.R
import com.spiderbiggen.manga.presentation.theme.MangaReaderTheme
import com.spiderbiggen.manga.presentation.ui.manga.model.MangaViewData

private const val ASPECT_RATIO = 0.741f
private val aspectModifier = Modifier
    .height(80.dp)
    .aspectRatio(ASPECT_RATIO, matchHeightConstraintsFirst = true)

@Composable
fun MangaRow(
    manga: MangaViewData,
    imageLoader: ImageLoader,
    navigateToManga: (MangaId) -> Unit,
    onClickFavorite: (MangaId) -> Unit,
    modifier: Modifier = Modifier,
) {
    ReadableCard(
        isRead = manga.readAll,
        onClick = dropUnlessResumed { navigateToManga(manga.id) },
        modifier = modifier,
    ) {
        Row(
            Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            CoverImage(manga.coverImage, imageLoader)
            MangaInfoColumn(manga, Modifier.weight(1f))
            IconRow(manga, onClickFavorite)
        }
    }
}

@Composable
private fun CoverImage(url: String, imageLoader: ImageLoader, modifier: Modifier = Modifier) {
    AsyncImage(
        model = url,
        imageLoader = imageLoader,
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = modifier.then(aspectModifier.clip(MaterialTheme.shapes.small)),
        alignment = Alignment.Center,
        placeholder = ColorPainter(MaterialTheme.colorScheme.surfaceContainerLow),
        error = if (LocalInspectionMode.current) painterResource(R.mipmap.preview_cover_placeholder) else null,
    )
}

@Composable
private fun IconRow(
    manga: MangaViewData,
    onClickFavorite: (MangaId) -> Unit,
) {
    Row {
        if (manga.status == "Dropped") {
            Icon(
                Icons.Rounded.Warning,
                modifier = Modifier.padding(12.dp),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
            )
        }
        IconButton(onClick = dropUnlessResumed { onClickFavorite(manga.id) }) {
            Icon(
                if (manga.isFavorite) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                contentDescription = null,
            )
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

@Preview("Light")
@Preview("Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewManga(@PreviewParameter(MangaViewDataProvider::class) state: MangaViewData) {
    MangaReaderTheme {
        Surface {
            MangaRow(
                manga = state,
                modifier = Modifier.padding(16.dp),
                imageLoader = SingletonImageLoader.get(LocalContext.current),
                navigateToManga = {},
                onClickFavorite = {},
            )
        }
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
            MangaViewData(
                source = "Asura",
                id = MangaId("712dd47d646544338484357604d6cf81"),
                title = "Heavenly Martial God",
                coverImage = "https://www.asurascans.com/wp-content/uploads/2021/09/martialgod.jpg",
                status = "Dropped",
                updatedAt = "2023-04-23",
                isFavorite = true,
                readAll = true,
            ),
        )
}
