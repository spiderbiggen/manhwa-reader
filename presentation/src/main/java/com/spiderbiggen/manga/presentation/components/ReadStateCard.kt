package com.spiderbiggen.manga.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun ReadStateCard(
    isRead: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    val containerColor: Color by animateColorAsState(
        when {
            !isRead -> MaterialTheme.colorScheme.surfaceContainer
            else -> MaterialTheme.colorScheme.surfaceContainerLowest
        },
        label = "card color",
    )
    ElevatedCard(
        onClick = onClick,
        modifier = modifier,
        colors = CardDefaults.elevatedCardColors(containerColor = containerColor),
        content = {
            val contentColor by animateColorAsState(
                LocalContentColor.current.let {
                    if (isRead) it.copy(alpha = 0.7f) else it
                },
            )

            CompositionLocalProvider(LocalContentColor provides contentColor) {
                content()
            }
        },
    )
}
