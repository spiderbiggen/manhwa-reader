package com.spiderbiggen.manga.data.source.local.room.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.spiderbiggen.manga.data.source.local.room.MangaDatabase
import com.spiderbiggen.manga.data.source.local.room.model.chapter.LocalChapterEntity
import com.spiderbiggen.manga.data.source.local.room.model.manga.LocalMangaEntity
import com.spiderbiggen.manga.domain.model.id.ChapterId
import com.spiderbiggen.manga.domain.model.id.MangaId
import kotlin.time.Instant
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LocalChapterDaoTest {
    private lateinit var database: MangaDatabase
    private lateinit var chapterDao: LocalChapterDao

    @Before
    fun setUp() {
        database =
            Room.inMemoryDatabaseBuilder(
                    ApplicationProvider.getApplicationContext(),
                    MangaDatabase::class.java,
                )
                .build()
        chapterDao = database.localChapterDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `given_chapters_across_manga_when_getting_previous_ids_then_only_requested_ordering_is_returned`() =
        runTest {
            val mangaId = MangaId("manga")
            val otherMangaId = MangaId("other-manga")
            val targetId = ChapterId("target")
            val previousId = ChapterId("previous")

            database.localMangaDao().insert(listOf(manga(mangaId), manga(otherMangaId)))
            chapterDao.insert(
                listOf(
                    chapter(targetId, mangaId, index = 3, subIndex = 2),
                    chapter(previousId, mangaId, index = 2),
                    chapter(ChapterId("unrelated-ordering"), mangaId, index = 4),
                    chapter(
                        ChapterId("unrelated-ordering-reference"),
                        mangaId,
                        index = 4,
                        subIndex = 1,
                    ),
                    chapter(ChapterId("other-manga"), otherMangaId, index = 5),
                    chapter(
                        ChapterId("other-manga-reference"),
                        otherMangaId,
                        index = 5,
                        subIndex = 1,
                    ),
                )
            )

            assertEquals(listOf(previousId), chapterDao.getPreviousChapterIds(targetId))
        }

    private fun manga(id: MangaId) =
        LocalMangaEntity(
            id = id,
            source = "test",
            title = "Test",
            cover = "",
            dominantColor = null,
            description = "",
            status = "Ongoing",
            updatedAt = Instant.fromEpochSeconds(0),
        )

    private fun chapter(id: ChapterId, mangaId: MangaId, index: Int, subIndex: Int? = null) =
        LocalChapterEntity(
            id = id,
            mangaId = mangaId,
            index = index,
            subIndex = subIndex,
            updatedAt = Instant.fromEpochSeconds(0),
            imageChunks = 0,
        )
}
