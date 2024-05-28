package com.spiderbiggen.manga.data.repository

import android.content.SharedPreferences
import androidx.core.content.edit
import com.spiderbiggen.manga.domain.model.id.MangaId
import javax.inject.Inject

class FavoritesRepository @Inject constructor(
    private val sharedPreferences: SharedPreferences,
) {

    private companion object {
        private const val FAVORITES_KEY = "favorites"
    }

    var favorites: Set<MangaId>
        get() = sharedPreferences.getStringSet(FAVORITES_KEY, emptySet()).orEmpty().map(::MangaId).toSet()
        private set(value) = sharedPreferences.edit {
            putStringSet(FAVORITES_KEY, value.map(MangaId::inner).toSet())
        }

    fun isFavorite(id: MangaId) = id in favorites

    fun setFavorite(id: MangaId, isFavorite: Boolean) {
        if (isFavorite) {
            setFavorite(id)
        } else {
            clearFavorite(id)
        }
    }

    private fun setFavorite(id: MangaId) {
        favorites += id
    }

    private fun clearFavorite(id: MangaId) {
        favorites -= id
    }
}
