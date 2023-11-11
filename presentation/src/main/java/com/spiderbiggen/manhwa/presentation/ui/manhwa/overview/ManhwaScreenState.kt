package com.spiderbiggen.manhwa.presentation.ui.manhwa.overview

import com.spiderbiggen.manhwa.presentation.model.ManhwaViewData

sealed interface ManhwaScreenState {
    data object Loading : ManhwaScreenState
    data class Ready(val manhwa: List<ManhwaViewData>) : ManhwaScreenState
    data class Error(val message: String) : ManhwaScreenState
}