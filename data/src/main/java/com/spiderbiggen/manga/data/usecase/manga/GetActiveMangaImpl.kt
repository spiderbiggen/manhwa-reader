package com.spiderbiggen.manga.data.usecase.manga

import com.spiderbiggen.manga.data.source.local.repository.MangaRepository
import com.spiderbiggen.manga.data.usecase.either
import com.spiderbiggen.manga.domain.model.mapLeft
import com.spiderbiggen.manga.domain.usecase.manga.GetActiveManga
import javax.inject.Inject
import kotlinx.coroutines.flow.map

class GetActiveMangaImpl @Inject constructor(
    private val mangaRepository: MangaRepository,
) : GetActiveManga {
    override suspend fun invoke() = mangaRepository.getMangas()
        .either()
        .mapLeft { flow ->
            flow.map { it.filterNot { (manga, _) -> manga.status == "Dropped" } }
        }
}
