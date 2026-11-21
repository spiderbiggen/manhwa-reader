package com.spiderbiggen.manga.data.usecase.manga.mapper

import com.spiderbiggen.manga.data.source.remote.model.MangaEntity
import com.spiderbiggen.manga.domain.model.id.MangaId
import kotlin.time.Instant
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ToLocalMangaTest {
    private val toLocal = ToLocalManga()

    @Test
    fun `given remote manga with last chapter update when mapped then local manga keeps both timestamps`() {
        val updatedAt = Instant.fromEpochSeconds(500)
        val lastChapterUpdatedAt = Instant.fromEpochSeconds(100)

        val result = toLocal(remote(updatedAt, lastChapterUpdatedAt))

        assertEquals(MangaId("manga"), result.id)
        assertEquals(updatedAt, result.updatedAt)
        assertEquals(lastChapterUpdatedAt, result.lastChapterUpdatedAt)
    }

    @Test
    fun `given remote manga without chapters when mapped then last chapter update stays null`() {
        val result = toLocal(remote(Instant.fromEpochSeconds(500), lastChapterUpdatedAt = null))

        assertNull(result.lastChapterUpdatedAt)
    }

    private fun remote(updatedAt: Instant, lastChapterUpdatedAt: Instant?) =
        MangaEntity(
            source = "test",
            id = MangaId("manga"),
            title = "Test",
            cover = "",
            dominantColor = null,
            description = "",
            status = "Ongoing",
            updatedAt = updatedAt,
            lastChapterUpdatedAt = lastChapterUpdatedAt,
        )
}
