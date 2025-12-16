package com.spiderbiggen.manga.data.usecase.user

import com.spiderbiggen.manga.data.source.local.repository.AuthenticationRepository
import com.spiderbiggen.manga.data.source.local.repository.FavoritesRepository
import com.spiderbiggen.manga.data.source.local.repository.ReadRepository
import com.spiderbiggen.manga.data.source.remote.UserService
import com.spiderbiggen.manga.data.source.remote.model.user.FavoriteState
import com.spiderbiggen.manga.data.source.remote.model.user.ReadState
import com.spiderbiggen.manga.data.usecase.either
import com.spiderbiggen.manga.domain.model.AppError
import com.spiderbiggen.manga.domain.model.Either
import com.spiderbiggen.manga.domain.model.id.ChapterId
import com.spiderbiggen.manga.domain.model.id.MangaId
import com.spiderbiggen.manga.domain.usecase.user.SynchronizeWithRemote
import javax.inject.Inject
import javax.inject.Provider
import kotlin.time.Clock.System.now
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Instant
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

private val MIN_SYNC_INTERVAL = 5.minutes
private val SYNC_FALLBACK_TIME = Instant.fromEpochSeconds(0)

class SynchronizeWithRemoteImpl @Inject constructor(
    private val userService: Provider<UserService>,
    private val authenticationRepository: AuthenticationRepository,
    private val favoritesRepository: FavoritesRepository,
    private val readRepository: ReadRepository,
) : SynchronizeWithRemote {

    private val mutex = Mutex()

    override suspend operator fun invoke(ignoreInterval: Boolean): Either<Unit, AppError> = runCatching {
        if (mutex.isLocked) return@runCatching
        mutex.withLock {
            val authenticationState = authenticationRepository.getAuthenticatedState() ?: return@runCatching

            val lastSyncTime = authenticationState.lastSynchronizationTime ?: SYNC_FALLBACK_TIME
            val currentTime = now()
            if (!ignoreInterval && currentTime - lastSyncTime < MIN_SYNC_INTERVAL) return@runCatching

            val userService = userService.get()
            userService.syncFavorites(lastSyncTime)
            userService.syncReads(lastSyncTime)

            authenticationRepository.saveLastSynchronizationTime(currentTime)
        }
    }.either()

    private suspend fun UserService.syncFavorites(lastSyncTime: Instant) {
        val favorites = favoritesRepository.get(lastSyncTime).getOrThrow()
        if (favorites.isNotEmpty()) {
            val favoriteUpdates = favorites.associate { it.id to FavoriteState(it.isFavorite, it.updatedAt) }
            val receivedUpdates = updateFavorites(favoriteUpdates).sanitizeKeys()
            favoritesRepository.set(receivedUpdates).getOrThrow()
        }
        val receivedFavoriteUpdates = getFavorites(lastSyncTime).sanitizeKeys()
        favoritesRepository.set(receivedFavoriteUpdates).getOrThrow()
    }

    private suspend fun UserService.syncReads(lastSyncTime: Instant) {
        val reads = readRepository.get(lastSyncTime).getOrThrow()
        if (reads.isNotEmpty()) {
            val favoriteUpdates = reads.associate { it.id to ReadState(it.isRead, it.updatedAt) }
            val receivedUpdates = updateReadProgress(favoriteUpdates).sanitizeKeys()
            readRepository.set(receivedUpdates).getOrThrow()
        }
        val receivedReadUpdates = getReadProgress(lastSyncTime).sanitizeKeys()
        readRepository.set(receivedReadUpdates).getOrThrow()
    }

    @JvmName("sanitizeKeysManga")
    private fun <T> Map<MangaId, T>.sanitizeKeys(): Map<MangaId, T> =
        mapKeys { (key, _) -> MangaId(key.value.filter { it != '-' }) }

    @JvmName("sanitizeKeysChapter")
    private fun <T> Map<ChapterId, T>.sanitizeKeys(): Map<ChapterId, T> =
        mapKeys { (key, _) -> ChapterId(key.value.filter { it != '-' }) }
}
