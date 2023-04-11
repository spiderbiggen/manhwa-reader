package com.spiderbiggen.manhwa.presentation.ui.manhwa.overview

import com.spiderbiggen.manhwa.domain.model.Manhwa

sealed interface ManhwaScreenState {
    object Loading : ManhwaScreenState
    data class Ready(val manhwa: List<Manhwa>) : ManhwaScreenState
    data class Error(val error: Throwable) : ManhwaScreenState
}