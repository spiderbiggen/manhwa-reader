package com.spiderbiggen.manga.presentation.components

import androidx.compose.runtime.compositionLocalOf

/**
 * This `CompositionLocal` can be used by a screen to decide whether to display
 * a back button. Default is `true`. It is set to `false` when a screen is displayed
 * in a multi-pane layout where the back action is handled differently.
 */
val LocalBackButtonVisibility = compositionLocalOf { true }
