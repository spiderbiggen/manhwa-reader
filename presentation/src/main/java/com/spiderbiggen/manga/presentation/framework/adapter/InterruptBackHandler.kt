package com.spiderbiggen.manga.presentation.framework.adapter

import androidx.activity.compose.BackHandler
import androidx.activity.compose.PredictiveBackHandler
import androidx.compose.runtime.Composable
import com.spiderbiggen.manga.presentation.coverage.CoverageExcluded

@Composable
@CoverageExcluded
fun InterruptBackHandler(enabled: Boolean) {
    PredictiveBackHandler(enabled = enabled) { it.collect {} }
    BackHandler(enabled = enabled) {}
}
