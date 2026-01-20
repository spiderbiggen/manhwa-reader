package com.spiderbiggen.manga.data.source.remote.usecase

import arrow.core.Either
import arrow.core.raise.either
import com.spiderbiggen.manga.data.source.local.repository.AuthenticationRepository
import com.spiderbiggen.manga.data.source.remote.UserService
import com.spiderbiggen.manga.data.usecase.appError
import com.spiderbiggen.manga.data.usecase.user.MapUserEntity
import com.spiderbiggen.manga.domain.model.AppError
import com.spiderbiggen.manga.domain.model.auth.User

class FetchCurrentUser(
    private val userService: UserService,
    private val authenticationRepository: AuthenticationRepository,
    private val mapUserEntity: MapUserEntity,
) {
    suspend operator fun invoke(): Either<AppError, User> = either {
        val userEntity = appError { userService.getSelf() }
        val user = mapUserEntity(userEntity)
        val _ = authenticationRepository.saveUser(userEntity).bind()
        user
    }
}
