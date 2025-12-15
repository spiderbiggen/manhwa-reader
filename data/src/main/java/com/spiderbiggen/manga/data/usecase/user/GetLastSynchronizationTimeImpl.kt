package com.spiderbiggen.manga.data.usecase.user

import com.spiderbiggen.manga.data.source.local.repository.AuthenticationRepository
import com.spiderbiggen.manga.domain.usecase.user.GetLastSynchronizationTime
import javax.inject.Inject
import javax.inject.Provider

class GetLastSynchronizationTimeImpl @Inject constructor(
    private val authenticationRepository: Provider<AuthenticationRepository>,
) : GetLastSynchronizationTime {
    override fun invoke() = authenticationRepository.get().getLastSynchronizationTimeFlow()
}
