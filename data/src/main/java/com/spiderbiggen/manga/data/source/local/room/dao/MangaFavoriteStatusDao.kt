package com.spiderbiggen.manga.data.source.local.room.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.spiderbiggen.manga.data.source.local.room.model.manga.MangaFavoriteStatusEntity
import com.spiderbiggen.manga.domain.model.id.MangaId
import kotlinx.coroutines.flow.Flow

@Dao
interface MangaFavoriteStatusDao {
    @Upsert
    suspend fun insert(mangas: List<MangaFavoriteStatusEntity>)

    @Upsert
    suspend fun insert(manga: MangaFavoriteStatusEntity)

    @Query("SELECT is_favorite FROM manga_favorite_status WHERE id = :id")
    suspend fun isFavorite(id: MangaId): Boolean?

    @Query("SELECT is_favorite FROM manga_favorite_status WHERE id = :id")
    fun isFavoriteFlow(id: MangaId): Flow<Boolean?>
}
