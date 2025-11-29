package com.spiderbiggen.manga.presentation.ui.manga.list

import com.spiderbiggen.manga.domain.model.manga.MangaForOverview
import javax.inject.Inject
import kotlin.time.Clock.System.now
import kotlin.time.Duration.Companion.days
import kotlin.time.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class SplitMangasIntoSections @Inject constructor() {
    /**
     * Assumes the list is sorted
     */
    operator fun invoke(
        mangas: List<MangaForOverview>,
        timeZone: TimeZone,
    ): List<Pair<String, List<MangaForOverview>>> {
        val now = now()
        val localNow = now.toLocalDateTime(timeZone)
        return mangas
            .groupBy { Grouping.fromTimes(now, localNow, it.manga.updatedAt, timeZone) }
            .mapKeys { (key, _) -> key.text }
            .toList()
    }
}

private enum class Grouping(val text: String) {
    Today("Today"),
    ThisWeek("This week"),
    ThisMonth("This month"),
    ThisYear("This year"),
    Older("Older"),
    ;

    companion object {
        fun fromTimes(now: Instant, localNow: LocalDateTime, value: Instant, timeZone: TimeZone): Grouping {
            val age = now - value
            if (age <= 1.days) return Today
            if (age <= 7.days) return ThisWeek
            val localValue = value.toLocalDateTime(timeZone)

            if (localNow.year == localValue.year) {
                if (localNow.month == localValue.month) return ThisMonth
                return ThisYear
            }
            return Older
        }
    }
}
