package com.spiderbiggen.manhwa.data.source.remote.repository

import com.spiderbiggen.manhwa.data.di.BaseUrl
import com.spiderbiggen.manhwa.data.source.remote.ManhwaService
import com.spiderbiggen.manhwa.data.source.remote.model.ChapterEntity
import com.spiderbiggen.manhwa.data.source.remote.model.ManhwaEntity
import com.spiderbiggen.manhwa.domain.model.Chapter
import com.spiderbiggen.manhwa.domain.model.Manhwa
import java.net.URL
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

@Singleton
class ManhwaRepository @Inject constructor(
    @BaseUrl private val baseUrl: String,
    private val service: Provider<ManhwaService>
) {

    private val manhwas: MutableMap<String, Manhwa> = mutableMapOf()
    private val manhwaHasChapters: MutableMap<String, List<String>> = mutableMapOf()
    private val chaptersCache: MutableMap<String, Chapter> = mutableMapOf()

    suspend fun getManhwas(): Result<List<Manhwa>> {
        return if (manhwas.isNotEmpty()) {
            Result.success(manhwas.values.toList())
        } else {
            loadManhwas()
        }
    }

    suspend fun getManhwa(manhwaId: String): Result<Manhwa?> {
        return getManhwas().map { manhwa -> manhwa.find { it.id == manhwaId } }
    }

    suspend fun loadManhwas(): Result<List<Manhwa>> = runCatching {
        val manhwa = service.get().getAllManhwas().map(::mapManhwaToDomain)
        manhwa.associateByTo(manhwas, Manhwa::id)
        manhwa
    }

    suspend fun getChapters(manhwaId: String): Result<List<Chapter>> =
        manhwaHasChapters[manhwaId]?.takeUnless { it.isEmpty() }
            ?.mapNotNull(chaptersCache::get)?.let { Result.success(it) }
            ?: loadChapters(manhwaId)

    suspend fun loadChapters(manhwaId: String): Result<List<Chapter>> = runCatching {
        val newChapters = service.get().getManhwaChapters(manhwaId).map(::mapChapterToDomain)
        manhwaHasChapters[manhwaId] = newChapters.map(Chapter::id)
        newChapters.associateByTo(chaptersCache, Chapter::id)
        newChapters
    }

    suspend fun getChapter(manhwaId: String, chapterId: String): Result<Chapter?> {
        return chaptersCache[chapterId]?.let { Result.success(it) }
            ?: getChapters(manhwaId).map { chapters -> chapters.find { it.id == chapterId } }
    }

    private fun mapManhwaToDomain(manhwa: ManhwaEntity) = Manhwa(
        source = manhwa.source,
        id = manhwa.id,
        title = manhwa.title.trim(),
        baseUrl = URL(manhwa.baseUrl),
        coverImage = URL("${baseUrl}manhwas/${manhwa.id}/image"),
        description = manhwa.description?.trim(),
        status = manhwa.status.trim(),
        updatedAt = manhwa.updatedAt
    )

    private fun mapChapterToDomain(entity: ChapterEntity) = Chapter(
        id = entity.id,
        number = entity.number,
        decimal = entity.decimal,
        title = entity.title?.trim(),
        date = entity.date,
        images = entity.imageChunks?.let { chunks ->
            (0 until chunks).map {
                URL("${baseUrl}chapters/${entity.id}/images/$it")
            }
        }.orEmpty()
    )
}