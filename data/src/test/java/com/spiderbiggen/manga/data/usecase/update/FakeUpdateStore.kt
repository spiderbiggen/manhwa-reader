package com.spiderbiggen.manga.data.usecase.update

import arrow.core.Either
import arrow.core.right
import com.spiderbiggen.manga.data.source.local.preferences.CachedRelease
import com.spiderbiggen.manga.data.source.local.preferences.UpdatePreferences
import com.spiderbiggen.manga.data.source.local.repository.UpdateStore
import com.spiderbiggen.manga.domain.model.AppError
import kotlin.time.Instant
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeUpdateStore : UpdateStore {
    val state = MutableStateFlow(UpdatePreferences())

    override fun observe(): Flow<UpdatePreferences> = state

    override suspend fun getLastCheckedAt(): Either<AppError, Instant?> =
        state.value.lastCheckedAt.right()

    override suspend fun saveCheck(
        releases: List<CachedRelease>,
        checkedAt: Instant,
    ): Either<AppError, Unit> {
        state.value = UpdatePreferences(checkedAt, releases, dismissed = false)
        return Unit.right()
    }

    override suspend fun dismiss(): Either<AppError, Unit> {
        state.value = state.value.copy(dismissed = true)
        return Unit.right()
    }
}
