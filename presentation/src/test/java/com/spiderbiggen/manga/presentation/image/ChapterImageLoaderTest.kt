package com.spiderbiggen.manga.presentation.image

import java.io.File
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class ChapterImageLoaderTest {
    @Test
    fun `given a cache dir when resolving chapter cache then it is a dedicated child directory`() {
        val cacheDir = File("cache")

        assertEquals(
            File(cacheDir, CHAPTER_IMAGE_CACHE_DIRECTORY),
            chapterImageCacheDirectory(cacheDir),
        )
    }

    @Test
    fun `given the default coil cache directory when resolving chapter cache then they differ`() {
        assertNotEquals("image_cache", CHAPTER_IMAGE_CACHE_DIRECTORY)
    }
}
