package com.spiderbiggen.manga.data.usecase.manga

import com.spiderbiggen.manga.data.source.local.repository.MangaRepository
import com.spiderbiggen.manga.data.source.remote.usecase.GetRemoteMangaUseCase
import com.spiderbiggen.manga.data.usecase.either
import com.spiderbiggen.manga.data.usecase.manga.mapper.ToLocalMangaUseCase
import com.spiderbiggen.manga.domain.model.AppError
import com.spiderbiggen.manga.domain.model.Either
import com.spiderbiggen.manga.domain.model.andThenLeft
import com.spiderbiggen.manga.domain.usecase.remote.UpdateMangaFromRemote
import javax.inject.Inject
import javax.inject.Provider

class UpdateMangaFromRemoteImpl @Inject constructor(
    private val getRemoteManga: GetRemoteMangaUseCase,
    private val mangaRepository: Provider<MangaRepository>,
    private val toLocal: ToLocalMangaUseCase,
) : UpdateMangaFromRemote {
    override suspend operator fun invoke(skipCache: Boolean): Either<Unit, AppError> {
        val mangaRepository = mangaRepository.get()
        val updatedAt = mangaRepository.getLastUpdatedAt().getOrNull()
        return getRemoteManga(updatedAt, skipCache)
            .either()
            .andThenLeft { mangas ->
                mangaRepository.insert(mangas.map(toLocal::invoke)).either()
            }
    }
}
