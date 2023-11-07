package com.spiderbiggen.manhwa.data.source.remote.model

import kotlinx.datetime.LocalDate
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChapterEntity(
    val id: String,
    val number: Int,
    val decimal: Int?,
    val title: String?,
    val date: LocalDate,
    @SerialName("images")
    val images: Int
)