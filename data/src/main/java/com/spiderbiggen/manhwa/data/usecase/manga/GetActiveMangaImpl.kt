package com.spiderbiggen.manhwa.data.usecase.manga

import com.spiderbiggen.manhwa.data.source.local.repository.MangaRepository
import com.spiderbiggen.manhwa.data.usecase.either
import com.spiderbiggen.manhwa.domain.model.AppError
import com.spiderbiggen.manhwa.domain.model.Either
import com.spiderbiggen.manhwa.domain.model.Manga
import com.spiderbiggen.manhwa.domain.model.mapLeft
import com.spiderbiggen.manhwa.domain.usecase.manga.GetActiveManga
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetActiveMangaImpl @Inject constructor(
    private val mangaRepository: MangaRepository,
) : GetActiveManga {
    override suspend fun invoke() =
        mangaRepository.getMangas()
            .either()
            .mapLeft { flow ->
                flow.map { it.filterNot { (manga, _) -> manga.status == "Dropped" } }
            }
}