package com.spiderbiggen.manga.presentation.ui.manga.list.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.compose.dropUnlessStarted
import coil3.annotation.ExperimentalCoilApi
import coil3.asImage
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePreviewHandler
import coil3.compose.ConstraintsSizeResolver
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
    coverSizeResolver: ConstraintsSizeResolver = rememberConstraintsSizeResolver(),
) {
    Card(
        onClick = dropUnlessStarted { onMangaClick(manga.id) },
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        colors =
            CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
    ) {
        Box(modifier = Modifier.fillMaxWidth().aspectRatio(2f / 3f)) {
            AsyncImage(
                model =
                    ImageRequest.Builder(LocalPlatformContext.current)
                        .data(manga.coverImage)
                        .size(coverSizeResolver)
                        .build(),
                contentDescription = manga.title,
                contentScale = ContentScale.Crop,
                modifier =
                    Modifier.fillMaxSize()
                        .alpha(if (manga.isRead) 0.55f else 1f)
                        .then(coverSizeResolver),
            )

            // Gradient footer with title
            val coverAccentColor = manga.coverAccentColor
            Box(
                modifier =
                    Modifier.fillMaxWidth()
                        .fillMaxHeight(0.45f)
                        .align(Alignment.BottomCenter)
                        .background(
                            Brush.verticalGradient(
                                colorStops =
                                    arrayOf(
                                        0f to Color.Transparent,
                                        0.55f to coverAccentColor.copy(alpha = 0.65f),
                                        1f to coverAccentColor,
                                    )
                            )
                        )
                        .padding(horizontal = 6.dp, vertical = 8.dp),
                contentAlignment = Alignment.BottomStart,
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    val titleStyle =
                        when {
                            manga.isRead -> MaterialTheme.typography.titleSmall
                            else -> MaterialTheme.typography.titleSmallEmphasized
                        }
                    Text(
                        text = manga.title,
                        style =
                            titleStyle.copy(
                                shadow =
                                    Shadow(
                                        color = Color.Black,
                                        offset = Offset(0f, 2f),
                                        blurRadius = 4f,
                                    )
                            ),
                        color = Color.White,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                    LinearProgressIndicator(
                        progress = { manga.visualReadProgress },
                        modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = Color.White.copy(alpha = 0.35f),
                    )
                }
            }

            IconButton(
                modifier =
                    Modifier.align(Alignment.TopEnd)
                        .padding(4.dp)
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(manga.favoriteButtonBrush),
                onClick = dropUnlessStarted { onMangaFavoriteToggleClick(manga.id) },
            ) {
                FavoriteToggle(
                    isFavorite = manga.isFavorite,
                    contentColor = Color.White,
                    favoriteContentColor = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(20.dp),
                )
            }
        }
    }
}

val MangaViewData.coverAccentColor: Color
    @ReadOnlyComposable
    @Composable
    get() {
        val dominantColor = dominantColor ?: return MaterialTheme.colorScheme.scrim
        return Color(dominantColor)
            .copy(alpha = 0.75f)
            .compositeOver(MaterialTheme.colorScheme.scrim)
    }

val MangaViewData.favoriteButtonBrush: Brush
    @ReadOnlyComposable
    @Composable
    get() =
        Brush.radialGradient(
            colorStops =
                arrayOf(
                    0f to coverAccentColor.copy(alpha = 0.9f),
                    0.35f to coverAccentColor.copy(alpha = 0.7f),
                    0.7f to coverAccentColor.copy(alpha = 0.3f),
                    1f to Color.Transparent,
                )
        )

@OptIn(ExperimentalCoilApi::class)
@PreviewLightDark
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
                    modifier = Modifier.padding(8.dp).width(180.dp),
                )
            }
        }
    }
}
