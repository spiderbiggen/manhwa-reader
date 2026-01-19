package com.spiderbiggen.manga.data.usecase.user

import com.spiderbiggen.manga.data.source.local.repository.AuthenticationRepository
import com.spiderbiggen.manga.domain.usecase.user.GetLastSynchronizationTime

class GetLastSynchronizationTimeImpl(private val authenticationRepository: AuthenticationRepository) :
    GetLastSynchronizationTime {
    override fun invoke() = authenticationRepository.getLastSynchronizationTimeFlow()
}
