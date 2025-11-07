package com.spiderbiggen.manga.presentation.components

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.Card as MaterialCard
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ReadStateCard(
    isRead: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = RectangleShape,
    content: @Composable ColumnScope.() -> Unit,
) {
    val colors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
    ).let {
        when {
            isRead -> {
                val alpha = it.containerColor.alpha * 0.8f
                it.copy(contentColor = it.contentColor.copy(alpha = alpha))
            }
            else -> it
        }
    }
    MaterialCard(
        onClick = onClick,
        modifier = modifier,
        colors = colors,
        shape = shape,
        content = content,
    )
}
