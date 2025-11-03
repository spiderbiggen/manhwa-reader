package com.spiderbiggen.manga.presentation.components

import android.animation.ValueAnimator
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animation
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.LocalMotionScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.Group
import androidx.compose.ui.graphics.vector.Path
import androidx.compose.ui.graphics.vector.PathData
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.spiderbiggen.manga.presentation.theme.MangaReaderTheme

object FavoriteIconPaths {
    val Outline = PathData {
        moveTo(19.66f, 3.99f)
        curveToRelative(-2.64f, -1.8f, -5.9f, -0.96f, -7.66f, 1.1f)
        curveToRelative(-1.76f, -2.06f, -5.02f, -2.91f, -7.66f, -1.1f)
        curveToRelative(-1.4f, 0.96f, -2.28f, 2.58f, -2.34f, 4.29f)
        curveToRelative(-0.14f, 3.88f, 3.3f, 6.99f, 8.55f, 11.76f)
        lineToRelative(0.1f, 0.09f)
        curveToRelative(0.76f, 0.69f, 1.93f, 0.69f, 2.69f, -0.01f)
        lineToRelative(0.11f, -0.1f)
        curveToRelative(5.25f, -4.76f, 8.68f, -7.87f, 8.55f, -11.75f)
        curveToRelative(-0.06f, -1.7f, -0.94f, -3.32f, -2.34f, -4.28f)
        close()
        moveTo(12.1f, 18.55f)
        lineToRelative(-0.1f, 0.1f)
        lineToRelative(-0.1f, -0.1f)
        curveTo(7.14f, 14.24f, 4.0f, 11.39f, 4.0f, 8.5f)
        curveTo(4.0f, 6.5f, 5.5f, 5.0f, 7.5f, 5.0f)
        curveToRelative(1.54f, 0.0f, 3.04f, 0.99f, 3.57f, 2.36f)
        horizontalLineToRelative(1.87f)
        curveTo(13.46f, 5.99f, 14.96f, 5.0f, 16.5f, 5.0f)
        curveToRelative(2.0f, 0.0f, 3.5f, 1.5f, 3.5f, 3.5f)
        curveToRelative(0.0f, 2.89f, -3.14f, 5.74f, -7.9f, 10.05f)
        close()
    }

    val Fill = PathData {
        moveTo(13.35f, 20.13f)
        curveToRelative(-0.76f, 0.69f, -1.93f, 0.69f, -2.69f, -0.01f)
        lineToRelative(-0.11f, -0.1f)
        curveTo(5.3f, 15.27f, 1.87f, 12.16f, 2.0f, 8.28f)
        curveToRelative(0.06f, -1.7f, 0.93f, -3.33f, 2.34f, -4.29f)
        curveToRelative(2.64f, -1.8f, 5.9f, -0.96f, 7.66f, 1.1f)
        curveToRelative(1.76f, -2.06f, 5.02f, -2.91f, 7.66f, -1.1f)
        curveToRelative(1.41f, 0.96f, 2.28f, 2.59f, 2.34f, 4.29f)
        curveToRelative(0.14f, 3.88f, -3.3f, 6.99f, -8.55f, 11.76f)
        lineToRelative(-0.1f, 0.09f)
        close()
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun FavoriteToggle(
    isFavorite: Boolean,
    modifier: Modifier = Modifier,
) {
    val scale by animateFloatAsState(
        targetValue = if (isFavorite) 1f else 0f, label = "scale",
        animationSpec = LocalMotionScheme.current.fastSpatialSpec(),
    )
    val painter = rememberVectorPainter(
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f,
        autoMirror = true,
    ) { _, _ ->
        Group(
            name = "Outline",
        ) {
            Path(
                FavoriteIconPaths.Outline,
                fill = SolidColor(MaterialTheme.colorScheme.outline),
                strokeLineWidth = 1f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Bevel,
                strokeLineMiter = 1f,
            )
        }

        Group(
            name = "Fill",
            pivotX = 12f,
            pivotY = 12f,
            scaleX = scale,
            scaleY = scale,
        ) {
            Path(
                pathData = FavoriteIconPaths.Fill,
                fill = SolidColor(MaterialTheme.colorScheme.secondary),
                strokeLineWidth = 1f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Bevel,
                strokeLineMiter = 1f,
            )
        }
    }
    Icon(
        painter = painter,
        contentDescription = if (isFavorite) "Unfavorite" else "Favorite",
        modifier = modifier,
        tint = Color.Unspecified,
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Preview
@Composable
fun FavoriteTogglePreview() {
    MangaReaderTheme {
        var isFavorite by remember { mutableStateOf(true) }
        IconButton(onClick = { isFavorite = !isFavorite }) {
            FavoriteToggle(isFavorite = isFavorite)
        }
    }
}
