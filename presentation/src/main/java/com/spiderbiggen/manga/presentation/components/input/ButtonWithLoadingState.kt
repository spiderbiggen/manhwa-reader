package com.spiderbiggen.manga.presentation.components.input

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LoadingIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ButtonWithLoadingState(
    isLoading: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
) {
    if (isLoading) {
        LoadingIndicator(modifier)
    } else {
        Button(
            onClick = onClick,
            modifier = modifier,
            content = content,
        )
    }
}
