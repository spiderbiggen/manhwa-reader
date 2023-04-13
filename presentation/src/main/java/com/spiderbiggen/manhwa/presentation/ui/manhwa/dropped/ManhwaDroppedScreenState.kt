package com.spiderbiggen.manhwa.presentation.ui.manhwa.dropped

import com.spiderbiggen.manhwa.domain.model.Manhwa

sealed interface ManhwaDroppedScreenState {
    object Loading : ManhwaDroppedScreenState
    data class Ready(val manhwa: List<Manhwa>, val favorites: Set<String>) : ManhwaDroppedScreenState
    data class Error(val error: Throwable) : ManhwaDroppedScreenState
}