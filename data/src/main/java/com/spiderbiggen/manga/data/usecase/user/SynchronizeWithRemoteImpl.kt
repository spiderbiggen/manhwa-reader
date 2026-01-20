package com.spiderbiggen.manga.data.usecase.user

import arrow.core.Either
import arrow.core.raise.Raise
import arrow.core.raise.either
import arrow.fx.coroutines.parMapOrAccumulate
import arrow.fx.coroutines.parZip
import com.spiderbiggen.manga.data.source.local.repository.AuthenticationRepository
import com.spiderbiggen.manga.data.source.local.repository.FavoritesRepository
import com.spiderbiggen.manga.data.source.local.repository.ReadRepository
import com.spiderbiggen.manga.data.source.remote.UserService
import com.spiderbiggen.manga.data.source.remote.model.user.FavoriteState
import com.spiderbiggen.manga.data.source.remote.model.user.ReadState
import com.spiderbiggen.manga.data.usecase.appError
import com.spiderbiggen.manga.data.usecase.either
import com.spiderbiggen.manga.domain.model.AppError
import com.spiderbiggen.manga.domain.model.id.ChapterId
import com.spiderbiggen.manga.domain.model.id.MangaId
import com.spiderbiggen.manga.domain.usecase.user.SynchronizeWithRemote
import kotlin.time.Clock.System.now
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Instant
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

private val MIN_SYNC_INTERVAL = 5.minutes
private val SYNC_FALLBACK_TIME = Instant.fromEpochSeconds(0)

class SynchronizeWithRemoteImpl(
    private val userService: UserService,
    private val authenticationRepository: AuthenticationRepository,
    private val favoritesRepository: FavoritesRepository,
    private val readRepository: ReadRepository,
) : SynchronizeWithRemote {

    private val mutex = Mutex()

    override suspend operator fun invoke(ignoreInterval: Boolean): Either<AppError, Unit> = either {
        if (mutex.isLocked) return@either
        mutex.withLock {
            val authenticationState = appError {
                authenticationRepository.getAuthenticatedState()
            } ?: return@withLock

            val lastSyncTime = authenticationState.lastSynchronizationTime ?: SYNC_FALLBACK_TIME
            val currentTime = now()
            if (!ignoreInterval && currentTime - lastSyncTime < MIN_SYNC_INTERVAL) return@withLock

            parZip(
                { syncFavorites(lastSyncTime) },
                { syncReads(lastSyncTime) },
            ) { _, _ -> }
            appError { authenticationRepository.saveLastSynchronizationTime(currentTime) }
        }
    }

    private suspend fun Raise<AppError>.syncFavorites(lastSyncTime: Instant) {
        val favorites = favoritesRepository.get(lastSyncTime).either().bind()
        if (favorites.isNotEmpty()) {
            favorites
                .chunked(100) { chunk -> chunk.associate { it.id to FavoriteState(it.isFavorite, it.updatedAt) } }
                .parMapOrAccumulate { chunk ->
                    val receivedUpdates = appError { userService.updateFavorites(chunk) }.sanitizeKeys()
                    favoritesRepository.set(receivedUpdates).either().bind()
                }.onLeft { raise(if (it.size == 1) it.first() else AppError.Multi(it)) }
        }

        val receivedFavoriteUpdates = appError { userService.getFavorites(lastSyncTime) }.sanitizeKeys()
        favoritesRepository.set(receivedFavoriteUpdates).either().bind()
    }

    private suspend fun Raise<AppError>.syncReads(lastSyncTime: Instant) {
        val reads = readRepository.get(lastSyncTime).either().bind()
        if (reads.isNotEmpty()) {
            reads
                .chunked(100) { chunk -> chunk.associate { it.id to ReadState(it.isRead, it.updatedAt) } }
                .parMapOrAccumulate { chunk ->
                    val receivedUpdates = appError { userService.updateReadProgress(chunk) }.sanitizeKeys()
                    readRepository.set(receivedUpdates).either().bind()
                }.onLeft { raise(if (it.size == 1) it.first() else AppError.Multi(it)) }
        }

        val receivedReadUpdates = appError { userService.getReadProgress(lastSyncTime) }.sanitizeKeys()
        readRepository.set(receivedReadUpdates).either().bind()
    }

    @JvmName("sanitizeKeysManga")
    private fun <T> Map<MangaId, T>.sanitizeKeys(): Map<MangaId, T> =
        mapKeys { (key, _) -> MangaId(key.value.filter { it != '-' }) }

    @JvmName("sanitizeKeysChapter")
    private fun <T> Map<ChapterId, T>.sanitizeKeys(): Map<ChapterId, T> =
        mapKeys { (key, _) -> ChapterId(key.value.filter { it != '-' }) }
}
