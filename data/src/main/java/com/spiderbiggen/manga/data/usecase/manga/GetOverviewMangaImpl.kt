package com.spiderbiggen.manga.data.usecase.manga

import com.spiderbiggen.manga.data.source.local.repository.MangaRepository
import com.spiderbiggen.manga.data.usecase.either
import com.spiderbiggen.manga.domain.model.AppError
import com.spiderbiggen.manga.domain.model.Either
import com.spiderbiggen.manga.domain.model.manga.MangaForOverview
import com.spiderbiggen.manga.domain.usecase.manga.GetOverviewManga
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class GetOverviewMangaImpl @Inject constructor(private val mangaRepository: MangaRepository) : GetOverviewManga {
    override fun invoke(): Either<Flow<List<MangaForOverview>>, AppError> =
        mangaRepository.getMangasForOverview().either()
}
