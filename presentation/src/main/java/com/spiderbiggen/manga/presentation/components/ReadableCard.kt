package com.spiderbiggen.manga.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun ReadableCard(
    isRead: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    val containerColor: Color by animateColorAsState(
        if (!isRead) {
            MaterialTheme.colorScheme.surfaceContainer
        } else {
            MaterialTheme.colorScheme.surfaceContainerLowest
        },
        label = "card color",
    )
    ElevatedCard(
        onClick = onClick,
        modifier = modifier,
        colors = CardDefaults.elevatedCardColors(containerColor = containerColor),
        content = content,
    )
}
