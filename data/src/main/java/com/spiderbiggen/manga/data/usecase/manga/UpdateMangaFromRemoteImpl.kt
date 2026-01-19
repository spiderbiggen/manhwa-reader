package com.spiderbiggen.manga.data.usecase.manga

import com.spiderbiggen.manga.data.source.local.repository.MangaRepository
import com.spiderbiggen.manga.data.source.remote.usecase.GetRemoteMangaUseCase
import com.spiderbiggen.manga.data.usecase.either
import com.spiderbiggen.manga.data.usecase.manga.mapper.ToLocalMangaUseCase
import com.spiderbiggen.manga.domain.model.AppError
import com.spiderbiggen.manga.domain.model.Either
import com.spiderbiggen.manga.domain.model.andThenLeft
import com.spiderbiggen.manga.domain.usecase.remote.UpdateMangaFromRemote

class UpdateMangaFromRemoteImpl(
    private val getRemoteManga: GetRemoteMangaUseCase,
    private val mangaRepository: MangaRepository,
    private val toLocal: ToLocalMangaUseCase,
) : UpdateMangaFromRemote {
    override suspend operator fun invoke(skipCache: Boolean): Either<Unit, AppError> {
        val updatedAt = mangaRepository.getLastUpdatedAt().getOrNull()
        return getRemoteManga(updatedAt, skipCache)
            .either()
            .andThenLeft { mangas ->
                mangaRepository.insert(mangas.map(toLocal::invoke)).either()
            }
    }
}
