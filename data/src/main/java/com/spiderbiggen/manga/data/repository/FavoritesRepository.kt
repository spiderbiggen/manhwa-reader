package com.spiderbiggen.manga.data.repository

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

    fun isFavorite(mangaId: String) = mangaId in getFavorites()

    fun setFavorite(mangaId: String, isFavorite: Boolean) {
        if (isFavorite) {
            setFavorite(mangaId)
        } else {
            clearFavorite(mangaId)
        }
    }

    private fun setFavorite(mangaId: String) {
        val old = getFavorites()
        sharedPreferences.edit {
            putStringSet(FAVORITES_KEY, old + mangaId)
        }
    }

    private fun clearFavorite(mangaId: String) {
        val old = getFavorites()
        sharedPreferences.edit {
            putStringSet(FAVORITES_KEY, old - mangaId)
        }
    }
}
