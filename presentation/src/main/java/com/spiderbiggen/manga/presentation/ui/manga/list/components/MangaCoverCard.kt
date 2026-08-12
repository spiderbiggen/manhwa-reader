package com.spiderbiggen.manga.presentation.ui.manga.list.components

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.Wallpapers
import androidx.compose.ui.unit.dp
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.compose.dropUnlessStarted
import coil3.annotation.ExperimentalCoilApi
import coil3.asImage
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePreviewHandler
import coil3.compose.LocalAsyncImagePreviewHandler
import coil3.compose.LocalPlatformContext
import coil3.compose.rememberConstraintsSizeResolver
import coil3.request.ImageRequest
import com.spiderbiggen.manga.domain.model.id.MangaId
import com.spiderbiggen.manga.presentation.R
import com.spiderbiggen.manga.presentation.components.FavoriteToggle
import com.spiderbiggen.manga.presentation.theme.MangaReaderTheme
import com.spiderbiggen.manga.presentation.ui.manga.list.model.MangaViewData

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MangaCoverCard(
    manga: MangaViewData,
    onMangaClick: (MangaId) -> Unit,
    onMangaFavoriteToggleClick: (MangaId) -> Unit,
    modifier: Modifier = Modifier,
) {
    val footerColor =
        manga.dominantColor?.let { Color(it).copy(alpha = 0.88f) }
            ?: Color.Black.copy(alpha = 0.75f)

    Card(
        onClick = dropUnlessStarted { onMangaClick(manga.id) },
        modifier = modifier.alpha(if (manga.isRead) 0.55f else 1f),
        shape = MaterialTheme.shapes.medium,
        colors =
            CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
    ) {
        Box(modifier = Modifier.fillMaxWidth().aspectRatio(2f / 3f)) {
            val sizeResolver = rememberConstraintsSizeResolver()
            AsyncImage(
                model =
                    ImageRequest.Builder(LocalPlatformContext.current)
                        .data(manga.coverImage)
                        .size(sizeResolver)
                        .build(),
                contentDescription = manga.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize().then(sizeResolver),
            )

            // Gradient footer with title
            Box(
                modifier =
                    Modifier.fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .background(
                            Brush.verticalGradient(colors = listOf(Color.Transparent, footerColor))
                        )
                        .padding(horizontal = 6.dp, vertical = 8.dp),
                contentAlignment = Alignment.BottomStart,
            ) {
                Text(
                    text = manga.title,
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            IconButton(
                modifier = Modifier.align(Alignment.TopEnd).size(36.dp),
                onClick = dropUnlessStarted { onMangaFavoriteToggleClick(manga.id) },
            ) {
                FavoriteToggle(
                    isFavorite = manga.isFavorite,
                    contentColor = Color.White.copy(alpha = 0.75f),
                    favoriteContentColor = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(20.dp),
                )
            }
        }
    }
}

@OptIn(ExperimentalCoilApi::class)
@Preview("Light")
@Preview("Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview("Light - Red", wallpaper = Wallpapers.RED_DOMINATED_EXAMPLE)
@Preview(
    "Dark - Red",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    wallpaper = Wallpapers.RED_DOMINATED_EXAMPLE,
)
@Composable
private fun PreviewMangaCoverCard(
    @PreviewParameter(MangaViewDataProvider::class) state: MangaViewData
) {
    val context = LocalPlatformContext.current
    val previewHandler = AsyncImagePreviewHandler {
        ResourcesCompat.getDrawable(context.resources, R.mipmap.preview_cover_placeholder, null)!!
            .asImage()
    }
    CompositionLocalProvider(LocalAsyncImagePreviewHandler provides previewHandler) {
        MangaReaderTheme {
            Surface {
                MangaCoverCard(
                    manga = state,
                    onMangaClick = {},
                    onMangaFavoriteToggleClick = {},
                    modifier = Modifier.padding(8.dp).fillMaxWidth(0.33f),
                )
            }
        }
    }
}
