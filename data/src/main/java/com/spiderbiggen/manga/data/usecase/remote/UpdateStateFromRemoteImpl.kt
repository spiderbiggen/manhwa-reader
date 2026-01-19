package com.spiderbiggen.manga.data.usecase.remote

import com.spiderbiggen.manga.data.source.local.repository.FavoritesRepository
import com.spiderbiggen.manga.data.source.local.repository.MangaRepository
import com.spiderbiggen.manga.data.usecase.either
import com.spiderbiggen.manga.domain.model.AppError
import com.spiderbiggen.manga.domain.model.Either
import com.spiderbiggen.manga.domain.model.leftOrElse
import com.spiderbiggen.manga.domain.model.onRight
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

    override suspend fun invoke(skipCache: Boolean): Either<Unit, AppError> = coroutineScope {
        val deferredUserSync = async { synchronizeWithRemote(ignoreInterval = false) }
        val deferredMangaUpdate = async { updateMangaFromRemote(skipCache) }
        deferredUserSync.await()
        deferredMangaUpdate.await().onRight {
            return@coroutineScope Either.Right(it)
        }

        val outOfDataMangas = mangaRepository.getMangaForUpdate().either().leftOrElse {
            return@coroutineScope Either.Right(it)
        }

        val favoriteMangas = outOfDataMangas.filter { favoritesRepository.get(it).getOrDefault(false) }
        val appErrors = favoriteMangas
            .map { mangaId -> async { updateChaptersFromRemote(mangaId, skipCache) } }
            .awaitAll()
            .filterIsInstance<Either.Right<Unit, AppError>>()
            .map { it.value }

        when {
            appErrors.isEmpty() -> Either.Left(Unit)
            appErrors.size == 1 -> Either.Right(appErrors.first())
            else -> Either.Right(AppError.Multi(appErrors))
        }
    }
}
