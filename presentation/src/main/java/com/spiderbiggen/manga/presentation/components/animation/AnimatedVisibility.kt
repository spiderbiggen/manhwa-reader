package com.spiderbiggen.manga.presentation.components.animation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntSize

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ExpressiveAnimatedVisibility(
    visible: Boolean,
    modifier: Modifier = Modifier,
    label: String = "ExpressiveAnimatedVisibility",
    content: @Composable AnimatedVisibilityScope.() -> Unit,
) {
    val floatAnimationSpec = MaterialTheme.motionScheme.fastSpatialSpec<Float>()
    val intSizeAnimationSpec = MaterialTheme.motionScheme.fastSpatialSpec<IntSize>()
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = fadeIn(floatAnimationSpec) + expandIn(intSizeAnimationSpec),
        exit = shrinkOut(intSizeAnimationSpec) + fadeOut(floatAnimationSpec),
        label = label,
        content = content,
    )
}
