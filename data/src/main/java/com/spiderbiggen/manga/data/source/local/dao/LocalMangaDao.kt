package com.spiderbiggen.manga.data.source.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.spiderbiggen.manga.data.source.local.model.manga.LocalMangaEntity
import com.spiderbiggen.manga.data.source.local.model.manga.LocalMangaForOverviewEntity
import com.spiderbiggen.manga.data.source.local.model.manga.LocalMangaWithFavoriteStatus
import com.spiderbiggen.manga.domain.model.id.MangaId
import kotlinx.coroutines.flow.Flow

@Dao
interface LocalMangaDao {
    @Upsert
    suspend fun insert(chapter: List<LocalMangaEntity>)

    @Query("SELECT * FROM manga where id = :id")
    suspend fun get(id: MangaId): LocalMangaEntity?

    @Query(
        """
        SELECT *, COALESCE(f.is_favorite, 0) as is_favorite
        FROM manga m
            LEFT JOIN manga_favorite_status f on f.id = m.id
        WHERE m.id = :id
        """,
    )
    fun getWithFavorite(id: MangaId): Flow<LocalMangaWithFavoriteStatus?>

    @Query("SELECT * FROM manga WHERE source = :source")
    suspend fun getForSource(source: String): List<LocalMangaEntity>

    @Query(
        """
        SELECT DISTINCT m.*, COALESCE(f.is_favorite, 0) as is_favorite, COALESCE(r.is_read, 0) as is_read, c.id as chapter_id
        FROM manga m 
            LEFT JOIN manga_favorite_status f on f.id = m.id
            LEFT JOIN chapter c on c.manga_id = m.id AND c.updated_at = m.updated_at
            LEFT JOIN (
                SELECT c.manga_id, MIN(COALESCE(is_read, 0)) as is_read 
                    FROM manga m 
                    JOIN chapter c ON m.id = c.manga_id
                    LEFT JOIN chapter_read_status r ON c.id = r.id
                GROUP BY c.manga_id
            ) r ON r.manga_id = m.id
        WHERE m.status <> 'Dropped'
        ORDER BY updated_at DESC
        """,
    )
    fun getAllNotDropped(): Flow<List<LocalMangaForOverviewEntity>>

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
