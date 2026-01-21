package com.spiderbiggen.manga.data.usecase.manga

import arrow.core.Either
import arrow.core.getOrElse
import arrow.core.raise.either
import com.spiderbiggen.manga.data.source.local.repository.MangaRepository
import com.spiderbiggen.manga.data.source.remote.usecase.GetRemoteManga
import com.spiderbiggen.manga.data.usecase.manga.mapper.ToLocalManga
import com.spiderbiggen.manga.domain.model.AppError
import com.spiderbiggen.manga.domain.usecase.remote.UpdateMangaFromRemote

class UpdateMangaFromRemoteImpl(
    private val getRemoteManga: GetRemoteManga,
    private val mangaRepository: MangaRepository,
    private val toLocal: ToLocalManga,
) : UpdateMangaFromRemote {
    override suspend operator fun invoke(skipCache: Boolean): Either<AppError, Unit> = either {
        val updatedAt = mangaRepository.getLastUpdatedAt().getOrElse { null }
        val mangas = getRemoteManga(updatedAt, skipCache).bind()
        mangaRepository.insert(mangas.map(toLocal::invoke)).bind()
    }
}
