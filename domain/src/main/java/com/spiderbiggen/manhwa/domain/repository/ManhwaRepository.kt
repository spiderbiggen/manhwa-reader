package com.spiderbiggen.manhwa.domain.repository

import com.spiderbiggen.manhwa.domain.model.Chapter
import com.spiderbiggen.manhwa.domain.model.ChapterWithImageChunks
import com.spiderbiggen.manhwa.domain.model.Manhwa
import kotlinx.coroutines.flow.Flow

interface ManhwaRepository {
    fun getAll(): Flow<List<Manhwa>>
    fun getSingleFlow(id: String): Flow<Pair<Manhwa, List<Chapter>>>
    fun getChapterById(chapterId: String): Flow<ChapterWithImageChunks>
}