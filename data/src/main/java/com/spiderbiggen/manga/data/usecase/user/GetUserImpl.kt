package com.spiderbiggen.manga.data.usecase.user

import com.spiderbiggen.manga.data.source.local.repository.AuthenticationRepository
import com.spiderbiggen.manga.domain.model.auth.User
import com.spiderbiggen.manga.domain.usecase.user.GetUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetUserImpl(
    private val authenticationRepository: AuthenticationRepository,
    private val mapUserEntity: MapUserEntity,
) : GetUser {
    override fun invoke(): Flow<User?> = authenticationRepository.getUserFlow().map {
        it?.let { mapUserEntity(it) }
    }
}
