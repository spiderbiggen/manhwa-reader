package com.spiderbiggen.manga.presentation.ui.manga.chapter.overview.usecase

import com.spiderbiggen.manga.domain.model.Chapter
import com.spiderbiggen.manga.presentation.ui.manga.chapter.overview.model.ChapterRowData
import javax.inject.Inject
import kotlinx.datetime.LocalDate
import kotlinx.datetime.format
import kotlinx.datetime.format.DateTimeFormat
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.char

class MapChapterRowData @Inject constructor() {
    operator fun invoke(chapter: Chapter, isRead: Boolean) = ChapterRowData(
        id = chapter.id,
        index = chapter.index,
        subIndex = chapter.subIndex,
        title = chapter.title,
        date = chapter.date.format(dateFormat),
        isRead = isRead,
    )

    private companion object {
        private val dateFormat: DateTimeFormat<LocalDate> = LocalDate.Format {
            monthName(MonthNames.ENGLISH_FULL)
            char(' ')
            day(Padding.NONE)
            char(' ')
            year(Padding.NONE)
        }
    }
}
