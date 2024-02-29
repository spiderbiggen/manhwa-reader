package com.spiderbiggen.manhwa.data.usecase.read

import com.spiderbiggen.manhwa.domain.model.AppError
import com.spiderbiggen.manhwa.domain.model.Either
import com.spiderbiggen.manhwa.domain.model.leftOr
import com.spiderbiggen.manhwa.domain.model.leftOrElse
import com.spiderbiggen.manhwa.domain.usecase.chapter.GetChapters
import com.spiderbiggen.manhwa.domain.usecase.manga.GetManga
import com.spiderbiggen.manhwa.domain.usecase.read.IsRead
import com.spiderbiggen.manhwa.domain.usecase.read.MangaIsRead
import javax.inject.Inject

class MangaIsReadImpl @Inject constructor(
    private val isRead: IsRead,
    private val getManga: GetManga,
    private val getChapters: GetChapters,
) : MangaIsRead {
    override suspend fun invoke(mangaId: String): Either<Boolean, AppError> {
        val manga = getManga(mangaId).leftOrElse { return Either.Right(it) }
        val chapters = getChapters.once(mangaId).leftOrElse { return Either.Right(it) }
        val hasLastUpdate = chapters.any { it.updatedAt == manga.updatedAt }
        return Either.Left(hasLastUpdate && chapters.all { isRead(it.id).leftOr(false) })
    }
}
