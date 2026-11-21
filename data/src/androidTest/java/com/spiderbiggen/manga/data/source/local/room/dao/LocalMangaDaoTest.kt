package com.spiderbiggen.manga.data.source.local.room.dao

import androidx.room3.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.spiderbiggen.manga.data.source.local.room.MangaDatabase
import com.spiderbiggen.manga.data.source.local.room.model.chapter.ChapterReadStatusEntity
import com.spiderbiggen.manga.data.source.local.room.model.chapter.LocalChapterEntity
import com.spiderbiggen.manga.data.source.local.room.model.manga.LocalMangaEntity
import com.spiderbiggen.manga.domain.model.id.ChapterId
import com.spiderbiggen.manga.domain.model.id.MangaId
import kotlin.time.Instant
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LocalMangaDaoTest {
    private lateinit var database: MangaDatabase
    private lateinit var mangaDao: LocalMangaDao

    private val mangaId = MangaId("manga")
    private val chapterId = ChapterId("chapter")
    private val chapterUpdatedAt = Instant.fromEpochSeconds(100)

    @Before
    fun setUp() {
        database =
            Room.inMemoryDatabaseBuilder(
                    ApplicationProvider.getApplicationContext(),
                    MangaDatabase::class.java,
                )
                .build()
        mangaDao = database.localMangaDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `given manga updated without chapter changes when reading overview then manga stays read`() =
        runTest {
            givenReadChapter()
            mangaDao.insert(
                listOf(
                    manga(
                        updatedAt = Instant.fromEpochSeconds(500),
                        lastChapterUpdatedAt = chapterUpdatedAt,
                    )
                )
            )

            assertTrue(mangaDao.getAllNotDropped().first().single().isRead)
            assertTrue(mangaDao.getForUpdate().isEmpty())
        }

    @Test
    fun `given newer chapter on remote when reading overview then manga is unread and needs update`() =
        runTest {
            givenReadChapter()
            mangaDao.insert(
                listOf(
                    manga(
                        updatedAt = Instant.fromEpochSeconds(500),
                        lastChapterUpdatedAt = Instant.fromEpochSeconds(500),
                    )
                )
            )

            assertFalse(mangaDao.getAllNotDropped().first().single().isRead)
            assertEquals(listOf(mangaId), mangaDao.getForUpdate())
        }

    @Test
    fun `given manga without chapters upstream when asking for updates then it is not requested`() =
        runTest {
            mangaDao.insert(listOf(manga(updatedAt = Instant.fromEpochSeconds(500))))

            assertTrue(mangaDao.getForUpdate().isEmpty())
        }

    private suspend fun givenReadChapter() {
        mangaDao.insert(listOf(manga(updatedAt = chapterUpdatedAt)))
        database
            .localChapterDao()
            .insert(
                listOf(
                    LocalChapterEntity(
                        id = chapterId,
                        mangaId = mangaId,
                        index = 1,
                        subIndex = null,
                        updatedAt = chapterUpdatedAt,
                        imageChunks = 0,
                    )
                )
            )
        database
            .chapterReadStatusDao()
            .insert(ChapterReadStatusEntity(id = chapterId, isRead = true))
    }

    private fun manga(updatedAt: Instant, lastChapterUpdatedAt: Instant? = null) =
        LocalMangaEntity(
            id = mangaId,
            source = "test",
            title = "Test",
            cover = "",
            dominantColor = null,
            description = "",
            status = "Ongoing",
            updatedAt = updatedAt,
            lastChapterUpdatedAt = lastChapterUpdatedAt,
        )
}
