package com.spiderbiggen.manga.data.source.local.room.model.chapter

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.spiderbiggen.manga.data.source.local.room.model.manga.LocalMangaEntity
import com.spiderbiggen.manga.domain.model.id.ChapterId
import com.spiderbiggen.manga.domain.model.id.MangaId
import kotlin.time.Instant
import kotlinx.datetime.LocalDate

@Entity(
    tableName = "chapter",
    foreignKeys = [
        ForeignKey(LocalMangaEntity::class, ["id"], ["manga_id"]),
    ],
    indices = [
        Index("manga_id"),
        Index("index_num", "sub_index"),
    ],
)
data class LocalChapterEntity(
    @PrimaryKey
    val id: ChapterId,
    @ColumnInfo("manga_id")
    val mangaId: MangaId,
    @ColumnInfo("index_num")
    val index: Int,
    @ColumnInfo("sub_index")
    val subIndex: Int? = null,
    val title: String? = null,
    val date: LocalDate,
    @ColumnInfo("updated_at")
    val updatedAt: Instant,
    @ColumnInfo("image_chunks", defaultValue = "0")
    val imageChunks: Int,
)
