package com.spiderbiggen.manhwa.data.usecase.manga

import com.spiderbiggen.manhwa.data.source.local.repository.MangaRepository
import com.spiderbiggen.manhwa.data.usecase.either
import com.spiderbiggen.manhwa.domain.model.mapLeft
import com.spiderbiggen.manhwa.domain.usecase.manga.GetDroppedManga
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetDroppedMangaImpl @Inject constructor(
    private val mangaRepository: MangaRepository,
) : GetDroppedManga {
    override suspend fun invoke() =
        mangaRepository.getMangas()
            .either()
            .mapLeft { flow ->
                flow.map { it.filter { (manga, _) -> manga.status == "Dropped" } }
            }
}