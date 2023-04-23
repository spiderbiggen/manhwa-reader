package com.spiderbiggen.manhwa.data.source.remote.repository

import android.content.SharedPreferences
import androidx.core.content.edit
import javax.inject.Inject

class FavoritesRepository @Inject constructor(
    private val sharedPreferences: SharedPreferences,
) {

    private companion object {
        private const val FAVORITES_KEY = "favorites"
    }

    fun getFavorites(): Set<String> =
        sharedPreferences.getStringSet(FAVORITES_KEY, emptySet()).orEmpty()

    fun isFavorite(manhwaId: String) =
        manhwaId in getFavorites()

    fun setFavorite(manhwaId: String, isFavorite: Boolean) {
        if (isFavorite) setFavorite(manhwaId)
        else clearFavorite(manhwaId)
    }

    private fun setFavorite(manhwaId: String) {
        val old = getFavorites()
        sharedPreferences.edit {
            putStringSet(FAVORITES_KEY, old + manhwaId)
        }
    }

    private fun clearFavorite(manhwaId: String) {
        val old = getFavorites()
        sharedPreferences.edit {
            putStringSet(FAVORITES_KEY, old - manhwaId)
        }
    }
}