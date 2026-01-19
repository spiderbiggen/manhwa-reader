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
import kotlin.time.Clock.System.now
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Instant
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
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

    override suspend operator fun invoke(ignoreInterval: Boolean): Either<Unit, AppError> = runCatching {
        if (mutex.isLocked) return@runCatching
        coroutineScope {
            mutex.withLock {
                val authenticationState = authenticationRepository.getAuthenticatedState() ?: return@coroutineScope

                val lastSyncTime = authenticationState.lastSynchronizationTime ?: SYNC_FALLBACK_TIME
                val currentTime = now()
                if (!ignoreInterval && currentTime - lastSyncTime < MIN_SYNC_INTERVAL) return@coroutineScope

                val deferredFavorites = async { userService.syncFavorites(lastSyncTime) }
                val deferredReads = async { userService.syncReads(lastSyncTime) }

                awaitAll(deferredFavorites, deferredReads)
                authenticationRepository.saveLastSynchronizationTime(currentTime)
            }
        }
    }.either()

    private suspend fun UserService.syncFavorites(lastSyncTime: Instant) = coroutineScope {
        favoritesRepository.get(lastSyncTime).getOrThrow()
            .chunked(100) { chunk -> chunk.associate { it.id to FavoriteState(it.isFavorite, it.updatedAt) } }
            .map {
                async {
                    val receivedUpdates = updateFavorites(it).sanitizeKeys()
                    favoritesRepository.set(receivedUpdates).getOrThrow()
                }
            }
            .awaitAll()

        val receivedFavoriteUpdates = getFavorites(lastSyncTime).sanitizeKeys()
        favoritesRepository.set(receivedFavoriteUpdates).getOrThrow()
    }

    private suspend fun UserService.syncReads(lastSyncTime: Instant) = coroutineScope {
        readRepository.get(lastSyncTime).getOrThrow()
            .chunked(100) { chunk -> chunk.associate { it.id to ReadState(it.isRead, it.updatedAt) } }
            .map {
                async {
                    val receivedUpdates = updateReadProgress(it).sanitizeKeys()
                    readRepository.set(receivedUpdates).getOrThrow()
                }
            }
            .awaitAll()

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
