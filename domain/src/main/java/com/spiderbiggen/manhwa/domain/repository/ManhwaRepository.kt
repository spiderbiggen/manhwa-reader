package com.spiderbiggen.manhwa.domain.repository

import com.spiderbiggen.manhwa.domain.model.Chapter
import com.spiderbiggen.manhwa.domain.model.Manhwa
import kotlinx.coroutines.flow.Flow
import java.net.URL

interface ManhwaRepository {
    fun flowAllManhwa(): Flow<List<Manhwa>>
    fun flowSingleManhwa(id: String): Flow<Pair<Manhwa, List<Chapter>>>

    fun getChapters(manhwaId: String): List<Chapter>
    fun getChapterById(chapterId: String): Chapter?
    suspend fun getChapterImages(chapterId: String): List<URL>
}