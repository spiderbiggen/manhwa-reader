package com.spiderbiggen.manhwa.data.source.remote.repository

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

class ManhwaRepositoryImpl @Inject constructor(
    private val service: Provider<ManhwaService>
) : ManhwaRepository {
    override fun getAll(): Flow<List<Manhwa>?> = flow {
        emit(service.get().getAllManhwas().body()?.map(::mapManhwaToDomain))
    }

    override fun getById(id: String): Flow<Pair<Manhwa, List<Chapter>>?> = flow {
        emit(service.get().getManhwaChapters(id).body()?.let(::mapWithChaptersToManhwa))
    }

    override fun getChapterById(chapterId: String): Flow<ChapterWithImageChunks?> = flow {
        val chapter = service.get().getChapter(chapterId)
        println(chapter)
        emit(chapter.body()?.let(::mapWithChunksToDomain))
    }

    private fun mapManhwaToDomain(manhwa: ManhwaEntity) = Manhwa(
        source = manhwa.source,
        id = manhwa.id,
        title = manhwa.title,
        baseUrl = URL(manhwa.baseUrl),
        coverImage = URL(manhwa.coverImage),
        description = manhwa.description,
        status = manhwa.status,
    )

    private fun mapWithChaptersToManhwa(entity: ManhwaChaptersResponseEntity): Pair<Manhwa, List<Chapter>> {
        val manhwa = Manhwa(
            source = entity.source,
            id = entity.id,
            title = entity.title,
            baseUrl = URL(entity.baseUrl),
            coverImage = URL(entity.coverImage),
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
            imageChunks = entity.imageChunks,
        )
}