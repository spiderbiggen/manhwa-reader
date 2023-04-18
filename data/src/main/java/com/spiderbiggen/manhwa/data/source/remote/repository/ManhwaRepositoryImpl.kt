package com.spiderbiggen.manhwa.data.source.remote.repository

import com.spiderbiggen.manhwa.data.di.BaseUrl
import com.spiderbiggen.manhwa.data.source.remote.ManhwaService
import com.spiderbiggen.manhwa.data.source.remote.model.ChapterEntity
import com.spiderbiggen.manhwa.data.source.remote.model.ManhwaEntity
import com.spiderbiggen.manhwa.domain.model.Chapter
import com.spiderbiggen.manhwa.domain.model.Manhwa
import com.spiderbiggen.manhwa.domain.repository.ManhwaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.net.URL
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

@Singleton
class ManhwaRepositoryImpl @Inject constructor(
    @BaseUrl private val baseUrl: String,
    private val service: Provider<ManhwaService>
) : ManhwaRepository {

    private val manhwas: MutableMap<String, Manhwa> = mutableMapOf()
    private val manhwaHasChapters: MutableMap<String, List<String>> = mutableMapOf()
    private val chaptersCache: MutableMap<String, Chapter> = mutableMapOf()

    private suspend fun getAllManhwa(): List<Manhwa> {
        val response = service.get().getAllManhwas()
        if (!response.isSuccessful) {
            return manhwas.values.toList()
        }
        val manhwa = response.body()?.map(::mapManhwaToDomain).orEmpty()
        manhwa.associateByTo(manhwas, Manhwa::id)
        return manhwa
    }

    private suspend fun getManhwa(id: String): Manhwa? =
        manhwas[id] ?: service.get().getManhwa(id).body()
            ?.let(::mapManhwaToDomain)
            ?.also { manhwas[id] = it }

    private suspend fun getChapters(manhwaId: String): List<Chapter> {
        val newChapters = service.get().getManhwaChapters(manhwaId).body()
            ?.map(::mapChapterToDomain)
            ?: return manhwaHasChapters[manhwaId]?.mapNotNull(chaptersCache::get).orEmpty()
        manhwaHasChapters[manhwaId] = newChapters.map(Chapter::id)
        newChapters.associateByTo(chaptersCache, Chapter::id)
        return newChapters
    }

    override suspend fun getChapter(manhwaId: String, chapterId: String): Chapter? =
        chaptersCache[chapterId] ?: run {
            getChapters(manhwaId)
            chaptersCache[chapterId]
        }

    override fun flowAllManhwa(): Flow<List<Manhwa>> = flow {
        emit(manhwas.values.sortedBy { it.title }.toList())
        emit(getAllManhwa().sortedBy { it.title })
    }

    override fun flowSingleManhwa(id: String): Flow<Pair<Manhwa, List<Chapter>>> = flow {
        var manhwa = manhwas[id]
        var chapters = manhwaHasChapters[id]?.mapNotNull(chaptersCache::get).orEmpty()
        if (manhwa != null) {
            emit(manhwa to chapters)
        }
        manhwa = getManhwa(id)
        manhwa?.let {
            emit(it to chapters)
        }
        chapters = getChapters(id)
        manhwa?.let {
            emit(it to chapters)
        }
    }

    override fun getCachedChapters(manhwaId: String): List<Chapter> =
        manhwaHasChapters[manhwaId]?.mapNotNull(chaptersCache::get).orEmpty()

    override fun getCachedChapter(chapterId: String): Chapter? =
        chaptersCache[chapterId]

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