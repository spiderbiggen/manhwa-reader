package com.spiderbiggen.manga.data.usecase.remote

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.raise.either
import arrow.fx.coroutines.parMapOrAccumulate
import arrow.fx.coroutines.parZip
import com.spiderbiggen.manga.data.source.local.repository.FavoritesRepository
import com.spiderbiggen.manga.data.source.local.repository.MangaRepository
import com.spiderbiggen.manga.data.usecase.appError
import com.spiderbiggen.manga.domain.model.AppError
import com.spiderbiggen.manga.domain.model.id.MangaId
import com.spiderbiggen.manga.domain.usecase.remote.UpdateChaptersFromRemote
import com.spiderbiggen.manga.domain.usecase.remote.UpdateMangaFromRemote
import com.spiderbiggen.manga.domain.usecase.remote.UpdateStateFromRemote
import com.spiderbiggen.manga.domain.usecase.user.SynchronizeWithRemote

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
        parZip(
            { synchronizeWithRemote(ignoreInterval = false).bind() },
            { updateMangaFromRemote(skipCache).bind() },
        ) { _, _ -> }

        val outOfDataMangas = appError {
            mangaRepository.getMangaForUpdate().getOrThrow()
        }

        val favoriteMangas = outOfDataMangas.filter { favoritesRepository.get(it).getOrDefault(false) }

        favoriteMangas.parMapOrAccumulate { mangaId ->
            updateChaptersFromRemote(mangaId, skipCache).bind()
        }.onLeft { errors ->
            raise(if (errors.size == 1) errors.first() else AppError.Multi(errors))
        }
        Unit
    }
}
