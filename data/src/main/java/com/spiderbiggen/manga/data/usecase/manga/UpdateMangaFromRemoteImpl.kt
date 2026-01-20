package com.spiderbiggen.manga.data.usecase.manga

import com.spiderbiggen.manga.data.source.local.repository.MangaRepository
import com.spiderbiggen.manga.data.source.remote.usecase.GetRemoteMangaUseCase
import com.spiderbiggen.manga.data.usecase.either
import com.spiderbiggen.manga.data.usecase.manga.mapper.ToLocalMangaUseCase
import com.spiderbiggen.manga.domain.model.AppError
import arrow.core.Either
import arrow.core.flatMap
import com.spiderbiggen.manga.domain.usecase.remote.UpdateMangaFromRemote

class UpdateMangaFromRemoteImpl(
    private val getRemoteManga: GetRemoteMangaUseCase,
    private val mangaRepository: MangaRepository,
    private val toLocal: ToLocalMangaUseCase,
) : UpdateMangaFromRemote {
    override suspend operator fun invoke(skipCache: Boolean): Either<AppError, Unit> {
        val updatedAt = mangaRepository.getLastUpdatedAt().getOrNull()
        return getRemoteManga(updatedAt, skipCache)
            .either()
            .flatMap { mangas ->
                mangaRepository.insert(mangas.map(toLocal::invoke)).either()
            }
    }
}
