package com.spiderbiggen.manga.data.source.local.model.chapter

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.spiderbiggen.manga.domain.model.id.ChapterId
import kotlin.time.Clock
import kotlin.time.Instant

@Entity(tableName = "chapter_read_status")
data class ChapterReadStatusEntity(
    @PrimaryKey
    val id: ChapterId,
    @ColumnInfo("is_read")
    val isRead: Boolean,
    @ColumnInfo("updated_at")
    val updatedAt: Instant = Clock.System.now(),
)
