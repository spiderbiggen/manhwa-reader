package com.spiderbiggen.manga.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ReadStateCard(
    isRead: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = CardDefaults.elevatedShape,
    content: @Composable ColumnScope.() -> Unit,
) {
    val animationSpec = MaterialTheme.motionScheme.fastEffectsSpec<Color>()
    val targetContainerColor = when {
        !isRead -> MaterialTheme.colorScheme.surfaceContainer
        else -> MaterialTheme.colorScheme.surfaceContainerLow
    }
    val containerColor: Color by animateColorAsState(
        targetContainerColor,
        label = "card color",
        animationSpec = animationSpec,
    )
    val contentColor by animateColorAsState(
        contentColorFor(targetContainerColor).let { if (isRead) it.copy(alpha = 0.7f) else it },
        label = "content color",
        animationSpec = animationSpec,
    )
    ElevatedCard(
        onClick = onClick,
        modifier = modifier,
        colors = CardDefaults.elevatedCardColors(
            containerColor = containerColor,
            contentColor = contentColor,
        ),
        shape = shape,
        content = content,
    )
}
