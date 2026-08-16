package com.spiderbiggen.manga.presentation.ui.manga.list

import com.spiderbiggen.manga.domain.model.id.ChapterId
import com.spiderbiggen.manga.domain.model.id.MangaId
import com.spiderbiggen.manga.domain.model.manga.Manga
import com.spiderbiggen.manga.domain.model.manga.MangaForOverview
import kotlin.time.Instant
import kotlinx.collections.immutable.persistentSetOf
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MangaFilterTest {
    @Test
    fun `given a read manga when unread filter matches then result is false`() {
        assertFalse(MangaFilter.Unread.matches(overview(isRead = true)))
    }

    @Test
    fun `given an unread manga when unread filter matches then result is true`() {
        assertTrue(MangaFilter.Unread.matches(overview(isRead = false)))
    }

    @Test
    fun `given a favorite manga when favorites filter matches then result is true`() {
        assertTrue(MangaFilter.Favorites.matches(overview(isFavorite = true)))
    }

    @Test
    fun `given a non favorite manga when favorites filter matches then result is false`() {
        assertFalse(MangaFilter.Favorites.matches(overview(isFavorite = false)))
    }

    @Test
    fun `given multiple active filters when all predicates match then manga is included`() {
        val filters = persistentSetOf(MangaFilter.Unread, MangaFilter.Favorites)

        assertTrue(filters.all { it.matches(overview(isRead = false, isFavorite = true)) })
    }

    @Test
    fun `given multiple active filters when one predicate does not match then manga is excluded`() {
        val filters = persistentSetOf(MangaFilter.Unread, MangaFilter.Favorites)

        assertFalse(filters.all { it.matches(overview(isRead = true, isFavorite = true)) })
    }

    private fun overview(
        isRead: Boolean = false,
        isFavorite: Boolean = false,
    ) =
        MangaForOverview(
            manga =
                Manga(
                    source = "test",
                    id = MangaId("test"),
                    title = "Test",
                    coverImage = "",
                    dominantColor = null,
                    description = null,
                    status = "Ongoing",
                    updatedAt = Instant.parse("2024-01-01T00:00:00Z"),
                ),
            isFavorite = isFavorite,
            isRead = isRead,
            lastChapterId = ChapterId("chapter"),
            readChapterCount = 0,
            totalChapterCount = 1,
        )
}
