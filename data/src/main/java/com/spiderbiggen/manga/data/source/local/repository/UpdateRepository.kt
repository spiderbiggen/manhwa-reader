package com.spiderbiggen.manga.data.source.local.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.dataStore
import arrow.core.Either
import arrow.core.raise.either
import com.spiderbiggen.manga.data.source.local.preferences.CachedRelease
import com.spiderbiggen.manga.data.source.local.preferences.UpdatePreferences
import com.spiderbiggen.manga.data.source.local.preferences.UpdatePreferencesSerializer
import com.spiderbiggen.manga.data.usecase.appError
import com.spiderbiggen.manga.domain.model.AppError
import kotlin.time.Instant
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

interface UpdateStore {
    fun observe(): Flow<UpdatePreferences>

    suspend fun getLastCheckedAt(): Either<AppError, Instant?>

    /** Remembers the outcome of a check and brings back a previously dismissed notice. */
    suspend fun saveCheck(releases: List<CachedRelease>, checkedAt: Instant): Either<AppError, Unit>

    suspend fun dismiss(): Either<AppError, Unit>
}

internal val Context.updateDataStore by
    dataStore(
        fileName = "update-preferences",
        serializer = UpdatePreferencesSerializer,
        corruptionHandler =
            ReplaceFileCorruptionHandler(
                produceNewData = { UpdatePreferencesSerializer.defaultValue }
            ),
    )

class UpdateRepository(private val dataStore: DataStore<UpdatePreferences>) : UpdateStore {

    override fun observe(): Flow<UpdatePreferences> = dataStore.data

    override suspend fun getLastCheckedAt(): Either<AppError, Instant?> = either {
        appError { dataStore.data.first().lastCheckedAt }
    }

    override suspend fun saveCheck(
        releases: List<CachedRelease>,
        checkedAt: Instant,
    ): Either<AppError, Unit> = update {
        it.copy(lastCheckedAt = checkedAt, releases = releases, noticeDismissed = false)
    }

    override suspend fun dismiss(): Either<AppError, Unit> = update {
        it.copy(noticeDismissed = true)
    }

    private suspend fun update(
        transform: (UpdatePreferences) -> UpdatePreferences
    ): Either<AppError, Unit> = either {
        appError { dataStore.updateData(transform) }
        Unit
    }
}
