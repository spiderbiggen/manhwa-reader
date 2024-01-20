package com.spiderbiggen.manhwa.data.source.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.spiderbiggen.manhwa.data.source.local.model.LocalMangaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LocalMangaDao {
    @Upsert
    suspend fun insert(chapter: List<LocalMangaEntity>)

    @Query("SELECT * FROM manga where id = :id")
    suspend fun get(id: String): LocalMangaEntity?

    @Query("SELECT * FROM manga WHERE source = :source")
    suspend fun getForSource(source: String): List<LocalMangaEntity>

    @Query("SELECT m.* FROM manga m LEFT JOIN chapter c on c.manga_id = m.id AND c.updated_at = m.updated_at ORDER BY updated_at DESC")
    fun getAll(): Flow<List<LocalMangaEntity>>

    @Query(
        """
        SELECT DISTINCT(m.id) FROM manga m
        WHERE m.updated_at > (SELECT MAX(updated_at) FROM chapter where manga_id = m.id)
        """
    )
    suspend fun getForUpdate(): List<String>
}