package com.spiderbiggen.manga.data.source.remote.model

import com.spiderbiggen.manga.domain.model.id.ChapterId
import kotlin.time.Instant
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChapterEntity(
    val id: ChapterId,
    val index: UInt,
    @SerialName("sub_index")
    val subIndex: UInt?,
    val title: String?,
    val date: LocalDate,
    @Contextual
    @SerialName("updated_at")
    val updatedAt: Instant,
    @SerialName("images")
    val images: UInt,
)
