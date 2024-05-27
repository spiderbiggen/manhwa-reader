package com.spiderbiggen.manga.data.usecase.manga

import com.spiderbiggen.manga.data.source.local.repository.MangaRepository
import com.spiderbiggen.manga.data.usecase.either
import com.spiderbiggen.manga.domain.model.mapLeft
import com.spiderbiggen.manga.domain.usecase.manga.GetDroppedManga
import javax.inject.Inject
import kotlinx.coroutines.flow.map

class GetDroppedMangaImpl @Inject constructor(
    private val mangaRepository: MangaRepository,
) : GetDroppedManga {
    override suspend fun invoke() = mangaRepository.getMangas()
        .either()
        .mapLeft { flow ->
            flow.map { it.filter { (manga, _) -> manga.status == "Dropped" } }
        }
}
