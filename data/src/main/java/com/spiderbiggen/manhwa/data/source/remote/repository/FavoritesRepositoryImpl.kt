package com.spiderbiggen.manhwa.data.source.remote.repository

import android.content.SharedPreferences
import androidx.core.content.edit
import com.spiderbiggen.manhwa.domain.repository.FavoritesRepository
import javax.inject.Inject

class FavoritesRepositoryImpl @Inject constructor(
    private val sharedPreferences: SharedPreferences,
) : FavoritesRepository {

    private companion object {
        private const val FAVORITES_KEY = "favorites"
    }

    override fun setFavorite(manhwaId: String, isFavorite: Boolean) {
        if (isFavorite) setFavorite(manhwaId)
        else clearFavorite(manhwaId)
    }

    private fun setFavorite(manhwaId: String) {
        val old = favorites()
        sharedPreferences.edit {
            putStringSet(FAVORITES_KEY, old + manhwaId)
        }
    }

    private fun clearFavorite(manhwaId: String) {
        val old = favorites()
        sharedPreferences.edit {
            putStringSet(FAVORITES_KEY, old - manhwaId)
        }
    }

    override fun isFavorite(manhwaId: String) =
        manhwaId in favorites()

    override fun favorites(): Set<String> =
        sharedPreferences.getStringSet(FAVORITES_KEY, emptySet()).orEmpty()
}