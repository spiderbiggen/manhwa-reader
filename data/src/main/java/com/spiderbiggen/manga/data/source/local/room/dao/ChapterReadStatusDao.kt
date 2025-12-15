package com.spiderbiggen.manga.data.source.local.room.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.spiderbiggen.manga.data.source.local.room.model.chapter.ChapterReadStatusEntity
import com.spiderbiggen.manga.domain.model.id.ChapterId
import kotlin.time.Instant
import kotlinx.coroutines.flow.Flow

@Dao
interface ChapterReadStatusDao {
    @Upsert
    suspend fun insert(chapter: List<ChapterReadStatusEntity>)

    @Upsert
    suspend fun insert(chapter: ChapterReadStatusEntity)

    @Query("SELECT * FROM chapter_read_status WHERE updated_at > :since")
    suspend fun get(since: Instant): List<ChapterReadStatusEntity>

    @Query("SELECT is_read FROM chapter_read_status WHERE id = :id")
    suspend fun isRead(id: ChapterId): Boolean?

    @Query("SELECT is_read FROM chapter_read_status WHERE id = :id")
    fun isReadFlow(id: ChapterId): Flow<Boolean?>
}
