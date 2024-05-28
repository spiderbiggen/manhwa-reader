package com.spiderbiggen.manga.data.source.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.spiderbiggen.manga.data.source.local.model.LocalMangaEntity
import com.spiderbiggen.manga.data.source.local.model.LocalMangaWithLastChapterIdEntity
import com.spiderbiggen.manga.domain.model.id.MangaId
import kotlinx.coroutines.flow.Flow

@Dao
interface LocalMangaDao {
    @Upsert
    suspend fun insert(chapter: List<LocalMangaEntity>)

    @Query("SELECT * FROM manga where id = :id")
    suspend fun get(id: MangaId): LocalMangaEntity?

    @Query("SELECT * FROM manga WHERE source = :source")
    suspend fun getForSource(source: String): List<LocalMangaEntity>

    @Query(
        """
        SELECT m.*, c.id as chapter_id
        FROM manga m 
            LEFT JOIN chapter c on c.manga_id = m.id AND c.updated_at = m.updated_at
        ORDER BY updated_at DESC
        """,
    )
    fun getAll(): Flow<List<LocalMangaWithLastChapterIdEntity>>

    @Query(
        """
        SELECT DISTINCT(m.id) 
        FROM manga m
            LEFT JOIN chapter c on c.manga_id = m.id AND c.updated_at = m.updated_at
        WHERE c.id IS NULL
        """,
    )
    suspend fun getForUpdate(): List<MangaId>
}
