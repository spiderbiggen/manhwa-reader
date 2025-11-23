package com.spiderbiggen.manga.presentation.components

import androidx.activity.compose.BackHandler
import androidx.activity.compose.PredictiveBackHandler
import androidx.compose.runtime.Composable

@Composable
fun InterruptBackHandler(enabled: Boolean) {
    PredictiveBackHandler(enabled = enabled) { it.collect { } }
    BackHandler(enabled = enabled) { }
}
