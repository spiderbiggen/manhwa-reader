package com.spiderbiggen.manhwa.presentation.ui.manhwa.favorites

import com.spiderbiggen.manhwa.domain.model.Manhwa

sealed interface ManhwaFavoritesScreenState {
    object Loading : ManhwaFavoritesScreenState
    data class Ready(val manhwa: List<Manhwa>) : ManhwaFavoritesScreenState
    data class Error(val error: Throwable) : ManhwaFavoritesScreenState
}