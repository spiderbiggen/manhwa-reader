package com.spiderbiggen.manga.domain.model.chapter

import com.spiderbiggen.manga.domain.model.id.ChapterId
import kotlin.time.Instant
import org.junit.Assert.assertEquals
import org.junit.Test

class ChapterTest {
    @Test
    fun `given an empty title when displaying a chapter then only the chapter number is shown`() {
        val chapter =
            Chapter(
                id = ChapterId("test"),
                index = 1u,
                title = "",
                updatedAt = Instant.fromEpochMilliseconds(0),
            )

        assertEquals("1", chapter.displayTitle())
    }
}
