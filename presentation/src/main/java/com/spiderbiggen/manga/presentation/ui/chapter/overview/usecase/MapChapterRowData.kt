package com.spiderbiggen.manga.presentation.ui.chapter.overview.usecase

import com.spiderbiggen.manga.domain.model.Chapter
import com.spiderbiggen.manga.presentation.ui.chapter.overview.model.ChapterRowData
import javax.inject.Inject
import kotlin.math.abs
import kotlinx.datetime.LocalDate
import kotlinx.datetime.format
import kotlinx.datetime.format.DateTimeFormat
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.char

class MapChapterRowData @Inject constructor() {
    operator fun invoke(chapter: Chapter, isRead: Boolean) = ChapterRowData(
        id = chapter.id,
        number = chapter.displayNumber(),
        isHalfNumber = abs(chapter.number - 0.5) % 1.0 < 1e-10,
        title = chapter.title,
        date = chapter.date.format(dateFormat),
        isRead = isRead,
    )

    private companion object {
        private val dateFormat: DateTimeFormat<LocalDate> = LocalDate.Format {
            monthName(MonthNames.ENGLISH_FULL)
            char(' ')
            dayOfMonth(Padding.NONE)
            char(' ')
            year(Padding.NONE)
        }
    }
}
