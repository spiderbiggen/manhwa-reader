package com.spiderbiggen.manga.presentation.components.snackbar

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarVisuals

data class SnackbarData(
    override val message: String,
    override val duration: SnackbarDuration = SnackbarDuration.Short,
) : SnackbarVisuals {
    // TODO support actions on snackbars
    override val actionLabel: String? = null
    override val withDismissAction: Boolean = false
}
