package com.spiderbiggen.manga.data.source.remote.usecase

import com.spiderbiggen.manga.data.source.local.repository.AuthenticationRepository
import com.spiderbiggen.manga.data.source.remote.UserService
import com.spiderbiggen.manga.data.source.remote.model.user.UserEntity
import com.spiderbiggen.manga.data.usecase.either
import com.spiderbiggen.manga.domain.model.AppError
import com.spiderbiggen.manga.domain.model.Either
import javax.inject.Inject
import javax.inject.Provider

class FetchCurrentUser @Inject constructor(
    private val userService: Provider<UserService>,
    private val authenticationRepository: Provider<AuthenticationRepository>,
) {
    suspend operator fun invoke(): Either<UserEntity, AppError> = runCatching {
        val userEntity = userService.get().getSelf()
        authenticationRepository.get().saveUser(userEntity)!!
    }.either()
}
