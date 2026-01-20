package com.spiderbiggen.manga.data.usecase.remote

import arrow.core.Either
import arrow.core.left
import arrow.core.raise.either
import arrow.core.right
import com.spiderbiggen.manga.data.source.local.repository.FavoritesRepository
import com.spiderbiggen.manga.data.source.local.repository.MangaRepository
import com.spiderbiggen.manga.data.usecase.appError
import com.spiderbiggen.manga.domain.model.AppError
import com.spiderbiggen.manga.domain.usecase.remote.UpdateChaptersFromRemote
import com.spiderbiggen.manga.domain.usecase.remote.UpdateMangaFromRemote
import com.spiderbiggen.manga.domain.usecase.remote.UpdateStateFromRemote
import com.spiderbiggen.manga.domain.usecase.user.SynchronizeWithRemote
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

class UpdateStateFromRemoteImpl(
    // Repositories
    private val favoritesRepository: FavoritesRepository,
    private val mangaRepository: MangaRepository,
    // Use-cases
    private val updateMangaFromRemote: UpdateMangaFromRemote,
    private val updateChaptersFromRemote: UpdateChaptersFromRemote,
    private val synchronizeWithRemote: SynchronizeWithRemote,
) : UpdateStateFromRemote {

    override suspend fun invoke(skipCache: Boolean): Either<AppError, Unit> = either {
        coroutineScope {
            val deferredUserSync = async { synchronizeWithRemote(ignoreInterval = false) }
            val deferredMangaUpdate = async { updateMangaFromRemote(skipCache) }
            deferredUserSync.await()
            deferredMangaUpdate.await().bind()

            val outOfDataMangas = appError {
                mangaRepository.getMangaForUpdate().getOrThrow()
            }

            val favoriteMangas = outOfDataMangas.filter { favoritesRepository.get(it).getOrDefault(false) }
            val appErrors = favoriteMangas
                .map { mangaId -> async { updateChaptersFromRemote(mangaId, skipCache) } }
                .awaitAll()
                .filterIsInstance<Either.Left<AppError>>()
                .map { it.value }

            if (appErrors.isNotEmpty()) {
                raise(if (appErrors.size == 1) appErrors.first() else AppError.Multi(appErrors))
            }
        }
    }
}
