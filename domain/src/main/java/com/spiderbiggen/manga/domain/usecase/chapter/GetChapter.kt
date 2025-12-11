package com.spiderbiggen.manga.domain.usecase.chapter

import com.spiderbiggen.manga.domain.model.chapter.ChapterForOverview
import com.spiderbiggen.manga.domain.model.id.ChapterId
import kotlinx.coroutines.flow.Flow

fun interface GetChapter {
    operator fun invoke(id: ChapterId): Flow<ChapterForOverview?>
}
