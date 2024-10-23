package com.spiderbiggen.manga.presentation.ui.manga.model

import com.spiderbiggen.manga.domain.model.id.ChapterId
import com.spiderbiggen.manga.domain.model.id.MangaId
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
sealed interface MangaRoutes {
    @Serializable
    data object Host

    @Serializable
    data object Explore : MangaRoutes

    @Serializable
    data object Favorites : MangaRoutes

    @Serializable
    class Chapters private constructor(private val _mangaId: String) : MangaRoutes {
        @Transient
        val mangaId: MangaId = MangaId(_mangaId)

        companion object {
            operator fun invoke(mangaId: MangaId) = Chapters(mangaId.inner)
        }

        @Serializable
        class Read private constructor(private val _mangaId: String, private val _chapterId: String) : MangaRoutes {
            @Transient
            val mangaId: MangaId = MangaId(_mangaId)

            @Transient
            val chapterId: ChapterId = ChapterId(_chapterId)

            companion object {
                operator fun invoke(mangaId: MangaId, chapterId: ChapterId) = Read(mangaId.inner, chapterId.inner)
            }
        }
    }
}
