package com.spiderbiggen.manga.presentation.ui.manga.list.components

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.tooling.preview.Wallpapers
import androidx.compose.ui.unit.dp
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.compose.dropUnlessStarted
import coil3.annotation.ExperimentalCoilApi
import coil3.asImage
import coil3.compose.AsyncImagePreviewHandler
import coil3.compose.ConstraintsSizeResolver
import coil3.compose.LocalAsyncImagePreviewHandler
import coil3.compose.LocalPlatformContext
import coil3.compose.rememberAsyncImagePainter
import coil3.compose.rememberConstraintsSizeResolver
import coil3.request.ImageRequest
import com.spiderbiggen.manga.domain.model.id.MangaId
import com.spiderbiggen.manga.presentation.R
import com.spiderbiggen.manga.presentation.components.FavoriteToggle
import com.spiderbiggen.manga.presentation.components.ReadStateCard
import com.spiderbiggen.manga.presentation.theme.MangaReaderTheme
import com.spiderbiggen.manga.presentation.ui.manga.list.model.MangaViewData

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MangaGridItem(
    manga: MangaViewData,
    onMangaClick: (MangaId) -> Unit,
    onMangaFavoriteToggleClick: (MangaId) -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.medium,
    coverSizeResolver: ConstraintsSizeResolver = rememberConstraintsSizeResolver(),
) {
    ReadStateCard(
        isRead = manga.isRead,
        onClick = { onMangaClick(manga.id) },
        shape = shape,
        modifier = modifier,
    ) {
        Column {
            Box {
                val asyncPainter = rememberAsyncImagePainter(
                    model = ImageRequest.Builder(LocalPlatformContext.current)
                        .data(manga.coverImage)
                        .size(coverSizeResolver)
                        .build(),
                )
                Image(
                    painter = asyncPainter,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(3f / 4f)
                        .clip(shape)
                        .then(coverSizeResolver),
                )
                IconButton(
                    onClick = dropUnlessStarted { onMangaFavoriteToggleClick(manga.id) },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .background(MaterialTheme.colorScheme.surfaceBright, shape = RoundedCornerShape(percent = 50)),
                ) {
                    FavoriteToggle(isFavorite = manga.isFavorite)
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                val titleStyle = when {
                    manga.isRead -> MaterialTheme.typography.titleMedium
                    else -> MaterialTheme.typography.titleMediumEmphasized
                }
                Text(
                    text = manga.title,
                    style = titleStyle,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    autoSize = TextAutoSize.StepBased(maxFontSize = titleStyle.fontSize),
                )
                manga.updatedAt?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalCoilApi::class)
@Preview("Light")
@Preview("Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview("Light - Red", wallpaper = Wallpapers.RED_DOMINATED_EXAMPLE)
@Preview("Dark - Red", uiMode = Configuration.UI_MODE_NIGHT_YES, wallpaper = Wallpapers.RED_DOMINATED_EXAMPLE)
@Composable
fun PreviewManga(@PreviewParameter(MangaViewDataProvider::class) state: MangaViewData) {
    val context = LocalPlatformContext.current
    val previewHandler = AsyncImagePreviewHandler {
        ResourcesCompat.getDrawable(context.resources, R.mipmap.preview_cover_placeholder, null)!!.asImage()
    }
    CompositionLocalProvider(LocalAsyncImagePreviewHandler provides previewHandler) {
        MangaReaderTheme {
            Surface {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    MangaGridItem(
                        manga = state,
                        onMangaClick = {},
                        onMangaFavoriteToggleClick = {},
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                    )
                }
            }
        }
    }
}

class MangaViewDataProvider : PreviewParameterProvider<MangaViewData> {

    override val values: Sequence<MangaViewData>
        get() {
            return sequenceOf(
                MangaViewData(
                    source = "Asura",
                    id = MangaId("1"),
                    title = TITLE,
                    coverImage = COVER_IMAGE,
                    status = "Ongoing",
                    updatedAt = DATE_STRING,
                    isFavorite = false,
                    isRead = false,
                ),
                MangaViewData(
                    source = "Asura",
                    id = MangaId("2"),
                    title = TITLE,
                    coverImage = COVER_IMAGE,
                    status = "Ongoing",
                    updatedAt = DATE_STRING,
                    isFavorite = true,
                    isRead = true,
                ),
            )
        }

    private companion object {
        private const val DATE_STRING = "2023-04-23"
        private const val TITLE = "Heavenly Martial God"
        private const val COVER_IMAGE = "https://www.asurascans.com/wp-content/uploads/2021/09/martialgod.jpg"
    }
}
