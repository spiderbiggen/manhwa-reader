package com.spiderbiggen.manhwa.domain.model

import kotlinx.datetime.LocalDate

data class Chapter(
    val id: String,
    val number: Int,
    val decimal: Int?,
    val title: String?,
    val date: LocalDate?,
    val hasImages: Boolean,
)
