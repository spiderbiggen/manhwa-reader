package com.spiderbiggen.manga.data.source.local.repository

import android.content.SharedPreferences
import androidx.core.content.edit
import com.spiderbiggen.manga.data.source.local.dao.MangaFavoriteStatusDao
import com.spiderbiggen.manga.data.source.local.model.manga.MangaFavoriteStatusEntity
import com.spiderbiggen.manga.domain.model.id.MangaId
import javax.inject.Inject
import javax.inject.Provider
import kotlin.time.Clock.System.now
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.runBlocking

class FavoritesRepository @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    private val favoritesDaoProvider: Provider<MangaFavoriteStatusDao>,
) {

    private companion object {
        private const val FAVORITES_KEY = "favorites"
    }

    init {
        // This is a temporary solution until the app is migrated to Room.
        runBlocking {
            val oldFavorites =
                sharedPreferences.getStringSet(FAVORITES_KEY, emptySet()).orEmpty().map(::MangaId).toSet()
            if (oldFavorites.isEmpty()) return@runBlocking

            val date = now()
            favoritesDaoProvider.get().insert(
                oldFavorites.map {
                    MangaFavoriteStatusEntity(
                        id = it,
                        isFavorite = true,
                        updatedAt = date,
                    )
                },
            )
            sharedPreferences.edit { remove(FAVORITES_KEY) }
        }
    }

    fun getFlow(id: MangaId): Result<Flow<Boolean?>> = runCatching {
        favoritesDaoProvider.get().isFavoriteFlow(id)
    }

    suspend fun get(id: MangaId): Result<Boolean> = runCatching {
        favoritesDaoProvider.get().isFavorite(id) == true
    }

    suspend fun set(id: MangaId, isFavorite: Boolean): Result<Unit> = runCatching {
        favoritesDaoProvider.get().insert(MangaFavoriteStatusEntity(id, isFavorite))
    }
}
