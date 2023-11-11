package com.spiderbiggen.manhwa.presentation.ui.manhwa.favorites

import com.spiderbiggen.manhwa.presentation.model.ManhwaViewData

sealed interface ManhwaFavoritesScreenState {
    data object Loading : ManhwaFavoritesScreenState
    data class Ready(val manhwa: List<ManhwaViewData>) : ManhwaFavoritesScreenState
    data class Error(val message: String) : ManhwaFavoritesScreenState
}