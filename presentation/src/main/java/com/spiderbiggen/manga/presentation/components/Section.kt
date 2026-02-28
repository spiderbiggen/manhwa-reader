package com.spiderbiggen.manga.presentation.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.GenericShape
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

object SectionHeaderContentType
private val NO_CORNER = CornerSize(0.dp)

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
inline fun <T> LazyListScope.section(
    header: String?,
    items: ImmutableList<T>,
    noinline key: ((item: T) -> Any)? = null,
    noinline contentType: (item: T) -> Any? = { null },
    crossinline content: @Composable LazyItemScope.(T, Shape) -> Unit,
) {
    header?.let {
        item(contentType = SectionHeaderContentType) {
            Text(
                it,
                style = MaterialTheme.typography.titleLargeEmphasized,
                modifier = Modifier.padding(bottom = 4.dp),
            )
        }
    }

    sectionItems(
        items = items,
        key = key,
        contentType = contentType,
        content = content,
    )
}

inline fun <T> LazyListScope.sectionItems(
    items: ImmutableList<T>,
    noinline key: ((item: T) -> Any)? = null,
    crossinline contentType: (item: T) -> Any? = { null },
    crossinline content: @Composable LazyItemScope.(T, Shape) -> Unit,
) {
    val lastIndex = items.lastIndex
    itemsIndexed(
        items = items,
        key = if (key != null) { _, it -> key(it) } else null,
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

        else -> combinedCornerShape(topShape, bottomShape, density)
    }
}

fun combinedCornerShape(topShape: CornerBasedShape, bottomShape: CornerBasedShape, density: Density): Shape =
    GenericShape { size: Size, layoutDirection: LayoutDirection ->
        val topPath = Path().apply {
            addOutline(
                topShape
                    .copy(bottomStart = NO_CORNER, bottomEnd = NO_CORNER)
                    .createOutline(size, layoutDirection, density),
            )
        }

        val bottomPath = Path().apply {
            addOutline(
                bottomShape
                    .copy(topStart = NO_CORNER, topEnd = NO_CORNER)
                    .createOutline(size, layoutDirection, density),
            )
        }

        addPath(topPath and bottomPath)
    }

object SectionDefaults {
    val largeShape: CornerBasedShape
        @Composable
        get() = MaterialTheme.shapes.medium

    val smallShape: CornerBasedShape
        @Composable
        get() = MaterialTheme.shapes.extraSmall
}
