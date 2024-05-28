package com.spiderbiggen.manga.domain.model

import com.spiderbiggen.manga.domain.model.id.ChapterId
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

data class Chapter(
    val id: ChapterId,
    val number: Double,
    val title: String?,
    val date: LocalDate,
    val updatedAt: Instant,
) {
    fun displayTitle(): String {
        // This is to show symbol . instead of ,
        val otherSymbols = DecimalFormatSymbols(Locale.ROOT)
        // Define the maximum number of decimals (number of symbols #)
        val df = DecimalFormat("#.##", otherSymbols)
        return buildString {
            append("Chapter ")
            append(df.format(number))
            title?.let {
                when {
                    it[0].isLetterOrDigit() -> append(" - ")
                    else -> append(' ')
                }
                append(it)
            }
        }
    }
}
