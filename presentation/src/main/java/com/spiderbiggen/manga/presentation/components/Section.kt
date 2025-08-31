package com.spiderbiggen.manga.presentation.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
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
    header: String,
    items: ImmutableList<T>,
    largeCornerSize: CornerSize,
    smallCornerSize: CornerSize,
    noinline key: ((item: T) -> Any)? = null,
    noinline contentType: (item: T) -> Any? = { null },
    crossinline content: @Composable LazyItemScope.(T, Shape) -> Unit,
) {
    val defaultShape: Shape = RoundedCornerShape(smallCornerSize)
    val topShape: Shape = RoundedCornerShape(
        topStart = largeCornerSize,
        topEnd = largeCornerSize,
        bottomStart = smallCornerSize,
        bottomEnd = smallCornerSize,
    )
    val bottomShape: Shape = RoundedCornerShape(
        topStart = smallCornerSize,
        topEnd = smallCornerSize,
        bottomStart = largeCornerSize,
        bottomEnd = largeCornerSize,
    )

    val lastIndex = items.lastIndex
    item {
        Text(
            header,
            style = MaterialTheme.typography.headlineMediumEmphasized,
            modifier = Modifier.padding(vertical = 4.dp),
        )
    }
    items(
        items.size,
        key = key?.let { key -> { key(items[it]) } },
        contentType = { contentType(items[it]) },
    ) { index ->
        val shape = when (index) {
            0 -> topShape
            lastIndex -> bottomShape
            else -> defaultShape
        }
        content(items[index], shape)
    }
}
