package com.spiderbiggen.manga.data.usecase.manga

import com.spiderbiggen.manga.data.source.local.repository.MangaRepository
import com.spiderbiggen.manga.domain.model.id.MangaId
import com.spiderbiggen.manga.domain.model.manga.MangaWithFavorite
import com.spiderbiggen.manga.domain.usecase.manga.GetManga
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class GetMangaImpl @Inject constructor(private val mangaRepository: MangaRepository) : GetManga {
    override fun invoke(id: MangaId): Flow<MangaWithFavorite?> = mangaRepository.getMangaWithFavoriteStatus(id)
}
