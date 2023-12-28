package com.spiderbiggen.manhwa.data.usecase.read

import com.spiderbiggen.manhwa.domain.model.AppError
import com.spiderbiggen.manhwa.domain.model.Either
import com.spiderbiggen.manhwa.domain.model.leftOr
import com.spiderbiggen.manhwa.domain.model.leftOrElse
import com.spiderbiggen.manhwa.domain.usecase.chapter.GetChapters
import com.spiderbiggen.manhwa.domain.usecase.read.IsManhwaRead
import com.spiderbiggen.manhwa.domain.usecase.read.IsRead
import javax.inject.Inject

class IsManhwaReadImpl @Inject constructor(
    private val isRead: IsRead,
    private val getChapters: GetChapters,
) : IsManhwaRead {
    override suspend fun invoke(manhwaId: String): Either<Boolean, AppError> {
        // TODO improve performance
        val chapters = getChapters.once(manhwaId).leftOrElse { return Either.Right(it) }
        return Either.Left(chapters.all { isRead(it.id).leftOr(false) })
    }
}