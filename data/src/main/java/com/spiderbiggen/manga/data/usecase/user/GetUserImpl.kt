package com.spiderbiggen.manga.data.usecase.user

import com.spiderbiggen.manga.data.source.local.repository.AuthenticationRepository
import com.spiderbiggen.manga.domain.model.auth.User
import com.spiderbiggen.manga.domain.usecase.user.GetUser
import javax.inject.Inject
import javax.inject.Provider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetUserImpl @Inject constructor(
    private val authenticationRepository: Provider<AuthenticationRepository>,
) : GetUser {
    override fun invoke(): Flow<User?> {
        return authenticationRepository.get().getUserFlow().map {
            // TODO add entity mapper use case
            it?.let { User(it.username, it.email, it.avatar) }
        }
    }
}
