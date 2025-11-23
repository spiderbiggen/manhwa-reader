package com.spiderbiggen.manga.data.source.remote.usecase

import com.spiderbiggen.manga.data.source.local.repository.AuthenticationRepository
import com.spiderbiggen.manga.data.source.remote.ProfileService
import com.spiderbiggen.manga.data.source.remote.model.UserEntity
import com.spiderbiggen.manga.data.usecase.either
import com.spiderbiggen.manga.domain.model.AppError
import com.spiderbiggen.manga.domain.model.Either
import javax.inject.Inject
import javax.inject.Provider
import retrofit2.HttpException

class FetchCurrentUser @Inject constructor(
    private val profileService: Provider<ProfileService>,
    private val authenticationRepository: Provider<AuthenticationRepository>,
) {
    suspend operator fun invoke(): Either<UserEntity, AppError> = runCatching {
        val response = profileService.get().getSelf()
        if (!response.isSuccessful) {
            throw HttpException(response)
        }
        val userEntity = response.body()!!
        authenticationRepository.get().saveUser(userEntity)!!
    }.either()
}
