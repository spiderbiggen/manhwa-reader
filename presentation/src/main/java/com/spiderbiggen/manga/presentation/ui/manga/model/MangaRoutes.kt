package com.spiderbiggen.manga.presentation.ui.manga.model

import com.spiderbiggen.manga.domain.model.id.ChapterId
import com.spiderbiggen.manga.domain.model.id.MangaId
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
sealed interface MangaRoutes {
    @Serializable
    data object Overview : MangaRoutes

    @Serializable
    data object Profile : MangaRoutes

    @Serializable
    data object Login : MangaRoutes

    @Serializable
    data object Registration: MangaRoutes

    @Serializable
    class Chapters private constructor(private val sMangaId: String) : MangaRoutes {
        @Transient
        val mangaId: MangaId = MangaId(sMangaId)

        companion object {
            operator fun invoke(mangaId: MangaId) = Chapters(mangaId.inner)
        }
    }

    @Serializable
    class Reader private constructor(private val sMangaId: String, private val sChapterId: String) : MangaRoutes {
        @Transient
        val mangaId: MangaId = MangaId(sMangaId)

        @Transient
        val chapterId: ChapterId = ChapterId(sChapterId)

        companion object {
            operator fun invoke(mangaId: MangaId, chapterId: ChapterId) = Reader(mangaId.inner, chapterId.inner)
        }
    }
}
