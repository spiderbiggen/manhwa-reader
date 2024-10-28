package com.spiderbiggen.manga.presentation.components

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toIntSize
import androidx.lifecycle.compose.dropUnlessStarted
import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.annotation.ExperimentalCoilApi
import coil3.asImage
import coil3.compose.AsyncImagePreviewHandler
import coil3.compose.LocalAsyncImagePreviewHandler
import coil3.compose.LocalPlatformContext
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.size.Size
import com.spiderbiggen.manga.domain.model.id.MangaId
import com.spiderbiggen.manga.presentation.R
import com.spiderbiggen.manga.presentation.theme.MangaReaderTheme
import com.spiderbiggen.manga.presentation.ui.manga.model.MangaViewData

private val COVER_SIZE = DpSize(60.dp, 80.dp)

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
        onClick = dropUnlessStarted { navigateToManga(manga.id) },
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
    val context = LocalPlatformContext.current
    val density = LocalDensity.current
    val asyncPainter = rememberAsyncImagePainter(
        model = remember(context) {
            val size = with(density) {
                val (width, height) = COVER_SIZE.toSize().toIntSize()
                Size(width, height)
            }
            ImageRequest.Builder(context)
                .data(url)
                .size(size)
                .build()
        },
        imageLoader = imageLoader,
    )
    Image(
        painter = asyncPainter,
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = modifier
            .size(COVER_SIZE)
            .clip(MaterialTheme.shapes.small),
    )
}

@Composable
private fun IconRow(manga: MangaViewData, onClickFavorite: (MangaId) -> Unit) {
    Row {
        if (manga.status == "Dropped") {
            Icon(
                Icons.Rounded.Warning,
                modifier = Modifier.padding(12.dp),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
            )
        }
        IconButton(onClick = dropUnlessStarted { onClickFavorite(manga.id) }) {
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

@OptIn(ExperimentalCoilApi::class)
@Preview("Light")
@Preview("Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewManga(@PreviewParameter(MangaViewDataProvider::class) state: MangaViewData) {
    val context = LocalPlatformContext.current
    val previewHandler = AsyncImagePreviewHandler {
        context.resources.getDrawable(R.mipmap.preview_cover_placeholder, null).asImage()
    }

    CompositionLocalProvider(LocalAsyncImagePreviewHandler provides previewHandler) {
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
