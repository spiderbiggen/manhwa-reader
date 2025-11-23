package com.spiderbiggen.manga.data.usecase.user

import com.spiderbiggen.manga.data.source.local.repository.AuthenticationRepository
import com.spiderbiggen.manga.domain.usecase.user.GetUser
import javax.inject.Inject
import javax.inject.Provider
import kotlinx.coroutines.flow.map

class GetUserImpl @Inject constructor(
    private val authenticationRepository: Provider<AuthenticationRepository>,
    private val mapUserEntity: MapUserEntity,
) : GetUser {
    override fun invoke() = authenticationRepository.get().getUserFlow().map { entity ->
        entity?.let { mapUserEntity(it) }
    }
}
