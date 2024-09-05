package com.spiderbiggen.manga.domain.model

import com.spiderbiggen.manga.domain.model.id.ChapterId
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate

data class Chapter(
    val id: ChapterId,
    val number: Double,
    val title: String?,
    val date: LocalDate,
    val updatedAt: Instant,
) {
    fun displayNumber(): String = df.format(number)

    fun displayTitle(): String = buildString {
        append(displayNumber())
        title?.let {
            when {
                it[0].isLetterOrDigit() -> append(" - ")
                else -> append(' ')
            }
            append(it)
        }
    }

    private companion object {
        // This is to show symbol . instead of ,
        val otherSymbols = DecimalFormatSymbols(Locale.ROOT)
        // Define the maximum number of decimals (number of symbols #)
        val df = DecimalFormat("#.##", otherSymbols)
    }
}
