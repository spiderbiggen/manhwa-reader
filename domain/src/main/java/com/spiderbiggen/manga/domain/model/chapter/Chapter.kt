package com.spiderbiggen.manga.domain.model.chapter

import com.spiderbiggen.manga.domain.model.id.ChapterId
import kotlin.time.Instant
import kotlinx.datetime.LocalDate

data class Chapter(
    val id: ChapterId,
    val index: UInt,
    val subIndex: UInt? = null,
    val title: String?,
    val date: LocalDate,
    val updatedAt: Instant,
) {
    fun displayTitle(): String = buildString {
        append(index)
        subIndex?.let {
            append('.')
            append(it)
        }

        title?.let {
            when {
                it[0].isLetterOrDigit() -> append(" - ")
                else -> append(' ')
            }
            append(it)
        }
    }
}
