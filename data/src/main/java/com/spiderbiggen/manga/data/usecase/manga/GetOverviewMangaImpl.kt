package com.spiderbiggen.manga.data.usecase.manga

import com.spiderbiggen.manga.data.source.local.repository.MangaRepository
import com.spiderbiggen.manga.domain.model.manga.MangaForOverview
import com.spiderbiggen.manga.domain.usecase.manga.GetOverviewManga
import kotlinx.coroutines.flow.Flow

class GetOverviewMangaImpl(private val mangaRepository: MangaRepository) : GetOverviewManga {
    override fun invoke(): Flow<List<MangaForOverview>> = mangaRepository.getMangasForOverview()
}
