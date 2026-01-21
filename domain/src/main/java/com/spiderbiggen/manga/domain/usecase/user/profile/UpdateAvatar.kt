package com.spiderbiggen.manga.domain.usecase.user.profile

import arrow.core.Either
import com.spiderbiggen.manga.domain.model.AppError
import java.net.URI

fun interface UpdateAvatar {
    suspend operator fun invoke(avatar: URI): Either<AppError, Unit>
}
