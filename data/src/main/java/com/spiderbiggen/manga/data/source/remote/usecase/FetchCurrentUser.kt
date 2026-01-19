package com.spiderbiggen.manga.data.source.remote.usecase

import com.spiderbiggen.manga.data.source.local.repository.AuthenticationRepository
import com.spiderbiggen.manga.data.source.remote.UserService
import com.spiderbiggen.manga.data.usecase.either
import com.spiderbiggen.manga.data.usecase.user.MapUserEntity
import com.spiderbiggen.manga.domain.model.AppError
import com.spiderbiggen.manga.domain.model.Either
import com.spiderbiggen.manga.domain.model.auth.User

class FetchCurrentUser(
    private val userService: UserService,
    private val authenticationRepository: AuthenticationRepository,
    private val mapUserEntity: MapUserEntity,
) {
    suspend operator fun invoke(): Either<User, AppError> = runCatching {
        val userEntity = userService.getSelf()
        val user = mapUserEntity(userEntity)
        authenticationRepository.saveUser(userEntity)
        user
    }.either()
}
