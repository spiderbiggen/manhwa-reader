package com.spiderbiggen.manga.domain.usecase.user.profile

import com.spiderbiggen.manga.domain.model.AppError
import com.spiderbiggen.manga.domain.model.Either
import java.net.URI

fun interface UpdateAvatar {
    suspend operator fun invoke(avatar: URI): Either<Unit, AppError>
}
