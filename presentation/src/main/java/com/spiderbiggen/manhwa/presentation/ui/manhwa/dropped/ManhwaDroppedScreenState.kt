package com.spiderbiggen.manhwa.presentation.ui.manhwa.dropped

import com.spiderbiggen.manhwa.presentation.model.ManhwaViewData

sealed interface ManhwaDroppedScreenState {
    object Loading : ManhwaDroppedScreenState
    data class Ready(val manhwa: List<ManhwaViewData>) : ManhwaDroppedScreenState
    data class Error(val message: String) : ManhwaDroppedScreenState
}