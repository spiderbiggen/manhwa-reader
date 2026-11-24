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
    fun given_manga_updated_without_chapter_changes_when_reading_overview_then_manga_stays_read() =
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
    fun given_newer_chapter_on_remote_when_reading_overview_then_manga_stays_read_and_needs_update() =
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

            assertTrue(mangaDao.getAllNotDropped().first().single().isRead)
            assertEquals(listOf(mangaId), mangaDao.getForUpdate())
        }

    @Test
    fun given_manga_without_chapters_upstream_when_asking_for_updates_then_it_is_not_requested() =
        runTest {
            mangaDao.insert(listOf(manga(updatedAt = Instant.fromEpochSeconds(500))))

            assertTrue(mangaDao.getForUpdate().isEmpty())
        }

    @Test
    fun given_manga_with_different_update_times_when_reading_overview_then_sorted_by_last_chapter_update() =
        runTest {
            mangaDao.insert(
                listOf(
                    manga(
                        id = "old-chapter",
                        updatedAt = Instant.fromEpochSeconds(900),
                        lastChapterUpdatedAt = Instant.fromEpochSeconds(100),
                    ),
                    manga(
                        id = "new-chapter",
                        updatedAt = Instant.fromEpochSeconds(200),
                        lastChapterUpdatedAt = Instant.fromEpochSeconds(800),
                    ),
                    manga(
                        id = "no-chapter",
                        updatedAt = Instant.fromEpochSeconds(500),
                    ),
                )
            )

            assertEquals(
                listOf("new-chapter", "no-chapter", "old-chapter"),
                mangaDao.getAllNotDropped().first().map { it.manga.id.value },
            )
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
        manga(mangaId.value, updatedAt, lastChapterUpdatedAt)

    private fun manga(id: String, updatedAt: Instant, lastChapterUpdatedAt: Instant? = null) =
        LocalMangaEntity(
            id = MangaId(id),
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
