package com.spiderbiggen.manga.presentation.components

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.vector.ImageVector

sealed interface AppDrawable {
    data class Resource(@DrawableRes val resId: Int) : AppDrawable
    data class Vector(val image: ImageVector) : AppDrawable
}
