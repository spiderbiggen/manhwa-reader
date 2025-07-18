package com.spiderbiggen.manga.data.usecase.read

import com.spiderbiggen.manga.domain.model.AppError
import com.spiderbiggen.manga.domain.model.Either
import com.spiderbiggen.manga.domain.model.id.MangaId
import com.spiderbiggen.manga.domain.model.leftOr
import com.spiderbiggen.manga.domain.model.leftOrElse
import com.spiderbiggen.manga.domain.usecase.chapter.GetChapters
import com.spiderbiggen.manga.domain.usecase.manga.GetManga
import com.spiderbiggen.manga.domain.usecase.read.IsRead
import com.spiderbiggen.manga.domain.usecase.read.MangaIsRead
import javax.inject.Inject

class MangaIsReadImpl @Inject constructor(
    private val isRead: IsRead,
    private val getManga: GetManga,
    private val getChapters: GetChapters,
) : MangaIsRead {
    override suspend fun invoke(id: MangaId): Either<Boolean, AppError> {
        val manga = getManga(id).leftOrElse { return Either.Right(it) }
        val chapters = getChapters.once(id).leftOrElse { return Either.Right(it) }
        val hasLastUpdate = chapters.any { it.updatedAt == manga.updatedAt }
        return Either.Left(hasLastUpdate && chapters.all { isRead(it.id).leftOr(false) })
    }
}
