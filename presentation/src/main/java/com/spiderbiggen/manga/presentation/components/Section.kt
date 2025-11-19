package com.spiderbiggen.manga.presentation.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
inline fun <T> LazyListScope.section(
    header: String?,
    items: ImmutableList<T>,
    largeCornerSize: CornerSize,
    smallCornerSize: CornerSize,
    noinline key: ((item: T) -> Any)? = null,
    noinline contentType: (item: T) -> Any? = { null },
    crossinline content: @Composable LazyItemScope.(T, Shape) -> Unit,
) {
    header?.let {
        item {
            Text(
                it,
                style = MaterialTheme.typography.headlineMediumEmphasized,
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
            val topCornerSize = if (index == 0) largeCornerSize else smallCornerSize
            val bottomCornerSize = if (index == lastIndex) largeCornerSize else smallCornerSize
            val shape = RoundedCornerShape(
                topStart = topCornerSize,
                topEnd = topCornerSize,
                bottomEnd = bottomCornerSize,
                bottomStart = bottomCornerSize,
            )
            content(it, shape)
        },
    )
}
