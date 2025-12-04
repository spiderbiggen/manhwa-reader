package com.spiderbiggen.manga.presentation.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.addOutline
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList

private val NO_CORNER = CornerSize(0.dp)
private val NO_CORNERS = Pair(NO_CORNER, NO_CORNER)

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
inline fun <T> LazyListScope.section(
    header: String?,
    items: ImmutableList<T>,
    noinline key: ((item: T) -> Any)? = null,
    noinline contentType: (item: T) -> Any? = { null },
    crossinline content: @Composable LazyItemScope.(T, Shape) -> Unit,
) {
    header?.let {
        item {
            Text(
                it,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 4.dp),
            )
        }
    }

    val lastIndex = items.lastIndex
    itemsIndexed(
        items = items,
        key = key?.let { key -> { _, it -> key(it) } },
        contentType = { _, it -> contentType(it) },
        itemContent = { index, it ->
            SectionItem(isFirst = index == 0, isLast = index == lastIndex) { shape ->
                content(it, shape)
            }
        },
    )
}

@Composable
fun SectionItem(
    isFirst: Boolean,
    isLast: Boolean,
    largeShape: CornerBasedShape = SectionDefaults.largeShape,
    smallShape: CornerBasedShape = SectionDefaults.smallShape,
    content: @Composable (Shape) -> Unit,
) {
    val topShape = if (isFirst) largeShape else smallShape
    val bottomShape = if (isLast) largeShape else smallShape

    content(fusedShape(topShape, bottomShape))
}

@Composable
fun fusedShape(
    topShape: CornerBasedShape,
    bottomShape: CornerBasedShape,
    density: Density = LocalDensity.current,
): Shape = remember(density, topShape, bottomShape) {
    when {
        topShape == bottomShape -> topShape

        topShape::class == bottomShape::class -> topShape.copy(
            bottomStart = bottomShape.bottomStart,
            bottomEnd = bottomShape.bottomEnd,
        )

        else -> GenericShape { size: Size, layoutDirection: LayoutDirection ->
            val (cutCornerTopStart, cutCornerTopEnd) =
                if (topShape is CutCornerShape) topShape.topStart to topShape.topEnd else NO_CORNERS
            val (cutCornerBottomStart, cutCornerBottomEnd) =
                if (bottomShape is CutCornerShape) bottomShape.bottomStart to bottomShape.bottomEnd else NO_CORNERS

            val (roundedCornerTopStart, roundedCornerTopEnd) =
                if (topShape is RoundedCornerShape) topShape.topStart to topShape.topEnd else NO_CORNERS
            val (roundedCornerBottomStart, roundedCornerBottomEnd) =
                if (bottomShape is RoundedCornerShape) bottomShape.bottomStart to bottomShape.bottomEnd else NO_CORNERS

            val cutoutOutline = CutCornerShape(
                topStart = cutCornerTopStart,
                topEnd = cutCornerTopEnd,
                bottomStart = cutCornerBottomStart,
                bottomEnd = cutCornerBottomEnd,
            ).createOutline(
                size,
                layoutDirection,
                density = density,
            )

            val roundedCornerOutline = RoundedCornerShape(
                topStart = roundedCornerTopStart,
                topEnd = roundedCornerTopEnd,
                bottomStart = roundedCornerBottomStart,
                bottomEnd = roundedCornerBottomEnd,
            ).createOutline(
                size = size,
                layoutDirection = layoutDirection,
                density = density,
            )

            val cutPath = Path().apply {
                addOutline(cutoutOutline)
            }

            val roundedPath = Path().apply {
                addOutline(roundedCornerOutline)
            }

            addPath(cutPath and roundedPath)
        }
    }
}

object SectionDefaults {
    val largeShape: CornerBasedShape
        @Composable
        get() = MaterialTheme.shapes.medium

    val smallShape: CornerBasedShape
        @Composable
        get() = MaterialTheme.shapes.extraSmall
}
