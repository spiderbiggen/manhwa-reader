package com.spiderbiggen.manga.presentation.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalLayoutDirection

@Composable
operator fun PaddingValues.plus(paddingValues: PaddingValues): PaddingValues {
    val layoutDirection = LocalLayoutDirection.current
    return PaddingValues(
        start = this.calculateStartPadding(layoutDirection) + paddingValues.calculateStartPadding(layoutDirection),
        top = this.calculateTopPadding() + paddingValues.calculateTopPadding(),
        end = this.calculateEndPadding(layoutDirection) + paddingValues.calculateEndPadding(layoutDirection),
        bottom = this.calculateBottomPadding() + paddingValues.calculateBottomPadding(),
    )
}
