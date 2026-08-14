package com.spiderbiggen.manga.data.source.local.room.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.spiderbiggen.manga.data.source.local.room.model.manga.LocalMangaEntity
import com.spiderbiggen.manga.data.source.local.room.model.manga.LocalMangaForOverviewEntity
import com.spiderbiggen.manga.data.source.local.room.model.manga.LocalMangaWithFavoriteStatus
import com.spiderbiggen.manga.domain.model.id.MangaId
import kotlin.time.Instant
import kotlinx.coroutines.flow.Flow

@Dao
interface LocalMangaDao {
    @Upsert suspend fun insert(chapter: List<LocalMangaEntity>)

    @Query(
        """
        SELECT *, COALESCE(f.is_favorite, 0) as is_favorite
        FROM manga m
            LEFT JOIN manga_favorite_status f on f.id = m.id
        WHERE m.id = :id
        """
    )
    fun getWithFavorite(id: MangaId): Flow<LocalMangaWithFavoriteStatus?>

    @Query(
        """
        SELECT m.*, COALESCE(f.is_favorite, 0) as is_favorite,
            MIN(COALESCE(r.is_read, 0), r.has_last_update) as is_read, c.id as chapter_id,
            COALESCE(r.read_chapter_count, 0) as read_chapter_count,
            COALESCE(r.total_chapter_count, 0) as total_chapter_count
        FROM manga m 
            LEFT JOIN manga_favorite_status f on f.id = m.id
            LEFT JOIN chapter c on c.manga_id = m.id AND c.updated_at = m.updated_at
            LEFT JOIN (
                SELECT c.manga_id,
                    MIN(COALESCE(r.is_read, 0)) as is_read,
                    MAX(m.updated_at = c.updated_at) as has_last_update,
                    CAST(SUM(CASE WHEN COALESCE(r.is_read, 0) = 1 THEN 1 ELSE 0 END) AS INTEGER) as read_chapter_count,
                    CAST(COUNT(c.id) AS INTEGER) as total_chapter_count
                    FROM manga m 
                    JOIN chapter c ON m.id = c.manga_id
                    LEFT JOIN chapter_read_status r ON c.id = r.id
                GROUP BY c.manga_id
            ) r ON r.manga_id = m.id
        WHERE m.status <> 'Dropped'
        ORDER BY updated_at DESC
        """
    )
    fun getAllNotDropped(): Flow<List<LocalMangaForOverviewEntity>>

    @Query(
        """
        SELECT DISTINCT(m.id) 
        FROM manga m
            LEFT JOIN chapter c on c.manga_id = m.id AND c.updated_at = m.updated_at
        WHERE c.id IS NULL
        """
    )
    suspend fun getForUpdate(): List<MangaId>

    @Query("SELECT updated_at FROM manga ORDER BY updated_at DESC LIMIT 1")
    suspend fun getLastUpdatedAt(): Instant?
}
