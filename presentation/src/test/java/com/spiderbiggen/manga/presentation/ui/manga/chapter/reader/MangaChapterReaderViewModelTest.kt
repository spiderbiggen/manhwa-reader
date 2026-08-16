package com.spiderbiggen.manga.presentation.ui.manga.chapter.reader

import arrow.core.left
import arrow.core.right
import com.spiderbiggen.manga.domain.model.AppError
import com.spiderbiggen.manga.domain.model.chapter.Chapter
import com.spiderbiggen.manga.domain.model.chapter.ChapterForOverview
import com.spiderbiggen.manga.domain.model.chapter.SurroundingChapters
import com.spiderbiggen.manga.domain.model.id.ChapterId
import com.spiderbiggen.manga.domain.model.id.MangaId
import com.spiderbiggen.manga.domain.usecase.chapter.GetChapter
import com.spiderbiggen.manga.domain.usecase.chapter.GetChapterImages
import com.spiderbiggen.manga.domain.usecase.chapter.GetSurroundingChapters
import com.spiderbiggen.manga.domain.usecase.favorite.IsFavoriteFlow
import com.spiderbiggen.manga.domain.usecase.favorite.ToggleFavorite
import com.spiderbiggen.manga.domain.usecase.read.SetRead
import com.spiderbiggen.manga.domain.usecase.read.SetReadUpToChapter
import com.spiderbiggen.manga.presentation.ui.manga.chapter.reader.navigation.MangaChapterReaderRoute
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.time.Instant
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MangaChapterReaderViewModelTest {
    @Test
    fun `given image loading fails when state is collected then error is emitted`() = runBlocking {
        val error = AppError.Remote.NotFound()
        val imageRequestStarted = AtomicBoolean(false)
        val viewModel =
            MangaChapterReaderViewModel(
                navKey = MangaChapterReaderRoute(MangaId("manga"), ChapterId("chapter")),
                getChapter = GetChapter { flowOf(chapterForOverview()) },
                getSurroundingChapters = GetSurroundingChapters { SurroundingChapters().right() },
                getChapterImages =
                    GetChapterImages {
                        imageRequestStarted.set(true)
                        error.left()
                    },
                isFavorite = IsFavoriteFlow { flowOf(false) },
                toggleFavorite = ToggleFavorite { false.right() },
                setRead = SetRead { _, _ -> Unit.right() },
                setReadUpToChapter = SetReadUpToChapter { Unit.right() },
            )

        var state: MangaChapterReaderScreenState? = null
        val collection = launch {
            viewModel.state.collect { currentState ->
                if (currentState is MangaChapterReaderScreenState.Error) {
                    state = currentState
                }
            }
        }
        withTimeout(1_000) {
            while (state == null) delay(10)
        }
        collection.cancel()

        assertTrue(imageRequestStarted.get())
        assertEquals(MangaChapterReaderScreenState.Error(error.message.orEmpty()), state)
    }

    private fun chapterForOverview() =
        ChapterForOverview(
            chapter =
                Chapter(
                    id = ChapterId("chapter"),
                    index = 1u,
                    title = null,
                    updatedAt = Instant.fromEpochSeconds(0),
                ),
            isRead = false,
        )
}
