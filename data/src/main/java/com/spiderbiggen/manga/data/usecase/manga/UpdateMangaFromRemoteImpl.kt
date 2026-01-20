package com.spiderbiggen.manga.data.usecase.manga

import arrow.core.Either
import arrow.core.raise.either
import com.spiderbiggen.manga.data.source.local.repository.MangaRepository
import com.spiderbiggen.manga.data.source.remote.usecase.GetRemoteMangaUseCase
import com.spiderbiggen.manga.data.usecase.appError
import com.spiderbiggen.manga.data.usecase.manga.mapper.ToLocalMangaUseCase
import com.spiderbiggen.manga.domain.model.AppError
import com.spiderbiggen.manga.domain.usecase.remote.UpdateMangaFromRemote

class UpdateMangaFromRemoteImpl(
    private val getRemoteManga: GetRemoteMangaUseCase,
    private val mangaRepository: MangaRepository,
    private val toLocal: ToLocalMangaUseCase,
) : UpdateMangaFromRemote {
    override suspend operator fun invoke(skipCache: Boolean): Either<AppError, Unit> = either {
        val updatedAt = mangaRepository.getLastUpdatedAt().getOrNull()
        val mangas = appError {
            getRemoteManga(updatedAt, skipCache).getOrThrow()
        }
        appError {
            mangaRepository.insert(mangas.map(toLocal::invoke)).getOrThrow()
        }
    }
}
