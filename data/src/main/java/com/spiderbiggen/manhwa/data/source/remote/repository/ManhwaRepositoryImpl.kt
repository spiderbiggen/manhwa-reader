package com.spiderbiggen.manhwa.data.source.remote.repository

import com.spiderbiggen.manhwa.data.di.BaseUrl
import com.spiderbiggen.manhwa.data.source.remote.ManhwaService
import com.spiderbiggen.manhwa.data.source.remote.model.ChapterEntity
import com.spiderbiggen.manhwa.data.source.remote.model.ChapterWithImageChunksEntity
import com.spiderbiggen.manhwa.data.source.remote.model.ManhwaChaptersResponseEntity
import com.spiderbiggen.manhwa.data.source.remote.model.ManhwaEntity
import com.spiderbiggen.manhwa.domain.model.Chapter
import com.spiderbiggen.manhwa.domain.model.ChapterWithImageChunks
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
    private val chapters: MutableMap<String, Chapter> = mutableMapOf()
    private val chapterImages: MutableMap<String, List<URL>> = mutableMapOf()

    override fun getAll(): Flow<List<Manhwa>> = flow {
        emit(manhwas.values.sortedBy { it.title }.toList())
        service.get().getAllManhwas().body()?.map(::mapManhwaToDomain)?.let { list ->
            emit(list.sortedBy { it.title })
            list.associateByTo(manhwas, Manhwa::id)
        }
    }

    override fun getSingleFlow(id: String): Flow<Pair<Manhwa, List<Chapter>>> = flow {
        manhwas[id]?.let { manhwa ->
            val chapters = manhwaHasChapters[id]?.let { chapterList ->
                chapterList.mapNotNull { chapters[it] }
            }.orEmpty()
            emit(manhwa to chapters)
        }
        service.get().getManhwaChapters(id).body()?.let(::mapWithChaptersToManhwa)?.let {
            emit(it)
            manhwaHasChapters[id] = it.second.map(Chapter::id)
            it.second.associateByTo(chapters, Chapter::id)
        }

    }

    override fun getChapterById(chapterId: String): Flow<ChapterWithImageChunks> = flow {
        chapters[chapterId]?.let {
            emit(mapToWithChunks(it, chapterImages[chapterId]))
        }
        service.get().getChapter(chapterId).body()?.let(::mapWithChunksToDomain)?.let {
            emit(it)
            chapterImages[chapterId] = it.imageChunks
        }
    }

    private fun mapManhwaToDomain(manhwa: ManhwaEntity) = Manhwa(
        source = manhwa.source,
        id = manhwa.id,
        title = manhwa.title,
        baseUrl = URL(manhwa.baseUrl),
        coverImage = URL("${baseUrl}manhwas/${manhwa.id}/image"),
        description = manhwa.description,
        status = manhwa.status,
    )

    private fun mapWithChaptersToManhwa(entity: ManhwaChaptersResponseEntity): Pair<Manhwa, List<Chapter>> {
        val manhwa = Manhwa(
            source = entity.source,
            id = entity.id,
            title = entity.title,
            baseUrl = URL(entity.baseUrl),
            coverImage = URL("${baseUrl}manhwas/${entity.id}/image"),
            description = entity.description,
            status = entity.status,
        )
        val chapters = entity.chapters.map(::mapChapterToDomain)
        return manhwa to chapters
    }

    private fun mapChapterToDomain(entity: ChapterEntity) = Chapter(
        id = entity.id,
        number = entity.number,
        decimal = entity.decimal,
        title = entity.title,
        url = URL(entity.url),
    )

    private fun mapWithChunksToDomain(entity: ChapterWithImageChunksEntity) =
        ChapterWithImageChunks(
            id = entity.id,
            number = entity.number,
            decimal = entity.decimal,
            title = entity.title,
            url = URL(entity.url),
            imageChunks = (0 until entity.imageChunks).map {
                URL("${baseUrl}chapters/${entity.id}/images/$it")
            },
        )

    private fun mapToWithChunks(entity: Chapter, chunks: List<URL>?) =
        ChapterWithImageChunks(
            id = entity.id,
            number = entity.number,
            decimal = entity.decimal,
            title = entity.title,
            url = entity.url,
            imageChunks = chunks.orEmpty(),
        )
}