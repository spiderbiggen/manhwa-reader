package com.spiderbiggen.manhwa.presentation.components

import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalAbsoluteTonalElevation
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.spiderbiggen.manhwa.presentation.model.MangaViewData

private const val aspectRatio = 1.35F
private val aspectModifier = Modifier
    .height(96.dp)
    .width(96.dp / aspectRatio)

@Composable
fun MangaRow(
    manga: MangaViewData,
    navigateToManga: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val readElevation = remember(manga.readAll) {
        if (manga.readAll) 1.dp else 4.dp
    }
    val localElevation = LocalAbsoluteTonalElevation.current + readElevation
    CompositionLocalProvider(
        LocalAbsoluteTonalElevation provides localElevation
    ) {
        Card(
            modifier
                .fillMaxWidth()
                .clickable { navigateToManga(manga.id) },
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
            ),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                AsyncImage(
                    model = manga.coverImage,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = aspectModifier,
                    alignment = Alignment.Center
                )
                Column(
                    Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically)
                ) {
                    Text(
                        manga.title,
                        style = MaterialTheme.typography.bodyLarge,
                    )
                    manga.updatedAt?.let {
                        Text(
                            it,
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                }
                if (manga.status == "Dropped") {
                    Icon(
                        Icons.Rounded.Warning,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                    )
                }
                Icon(
                    if (manga.isFavorite) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 16.dp)
                )
            }
        }
    }
}
