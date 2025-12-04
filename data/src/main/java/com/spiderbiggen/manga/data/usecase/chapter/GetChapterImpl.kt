package com.spiderbiggen.manga.data.usecase.chapter

import com.spiderbiggen.manga.data.source.local.repository.ChapterRepository
import com.spiderbiggen.manga.domain.model.chapter.ChapterForOverview
import com.spiderbiggen.manga.domain.model.id.ChapterId
import com.spiderbiggen.manga.domain.usecase.chapter.GetChapter
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class GetChapterImpl @Inject constructor(private val chapterRepository: ChapterRepository) : GetChapter {
    override fun invoke(id: ChapterId): Flow<ChapterForOverview?> = chapterRepository.getChapterAsFlow(id)
}
