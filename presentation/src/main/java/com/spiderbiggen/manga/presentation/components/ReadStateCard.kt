package com.spiderbiggen.manga.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.Card as MaterialCard
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape

val LocalReadStateAlpha = compositionLocalOf { 1f }

private const val READ_ALPHA = 0.8f

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ReadStateCard(
    isRead: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = RectangleShape,
    border: BorderStroke? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val readAlpha = if (isRead) READ_ALPHA else 1f
    val colors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
    ).let {
        if (isRead) it.copy(contentColor = it.contentColor.copy(alpha = it.contentColor.alpha * READ_ALPHA))
        else it
    }
    CompositionLocalProvider(LocalReadStateAlpha provides readAlpha) {
        MaterialCard(
            onClick = onClick,
            modifier = modifier,
            colors = colors,
            shape = shape,
            border = border,
            content = content,
        )
    }
}
