package com.spiderbiggen.manga.presentation.ui.manga.list.model

import com.spiderbiggen.manga.domain.model.id.MangaId
import org.junit.Assert.assertEquals
import org.junit.Test

class MangaViewDataTest {
    @Test
    fun `read progress is zero when manga has no chapters`() {
        assertEquals(0f, viewData(readChapterCount = 0, totalChapterCount = 0).readProgress)
    }

    @Test
    fun `read progress is calculated from read and total chapters`() {
        assertEquals(0.4f, viewData(readChapterCount = 2, totalChapterCount = 5).readProgress)
    }

    @Test
    fun `read progress is clamped when read count exceeds total chapters`() {
        assertEquals(1f, viewData(readChapterCount = 8, totalChapterCount = 5).readProgress)
    }

    @Test
    fun `visual read progress stays visibly incomplete when one chapter remains unread`() {
        assertEquals(
            0.95f,
            viewData(readChapterCount = 334, totalChapterCount = 335).visualReadProgress,
        )
    }

    @Test
    fun `visual read progress is full when all chapters are read`() {
        assertEquals(
            1f,
            viewData(readChapterCount = 335, totalChapterCount = 335).visualReadProgress,
        )
    }

    @Test
    fun `visual read progress is zero when manga has no chapters`() {
        assertEquals(0f, viewData(readChapterCount = 0, totalChapterCount = 0).visualReadProgress)
    }

    @Test
    fun `visual read progress treats over-count as complete`() {
        assertEquals(1f, viewData(readChapterCount = 8, totalChapterCount = 5).visualReadProgress)
    }

    private fun viewData(readChapterCount: Int, totalChapterCount: Int) =
        MangaViewData(
            id = MangaId("test"),
            source = "test",
            title = "Test",
            status = "Ongoing",
            coverImage = "",
            updatedAt = null,
            isFavorite = false,
            isRead = false,
            readChapterCount = readChapterCount,
            totalChapterCount = totalChapterCount,
            dominantColor = null,
        )
}
