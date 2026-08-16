package com.spiderbiggen.manga.presentation.ui.manga.list

import androidx.lifecycle.SavedStateHandle
import arrow.core.right
import com.spiderbiggen.manga.domain.model.id.ChapterId
import com.spiderbiggen.manga.domain.model.id.MangaId
import com.spiderbiggen.manga.domain.model.manga.Manga
import com.spiderbiggen.manga.domain.model.manga.MangaForOverview
import com.spiderbiggen.manga.domain.usecase.favorite.ToggleFavorite
import com.spiderbiggen.manga.domain.usecase.manga.GetOverviewManga
import com.spiderbiggen.manga.domain.usecase.remote.UpdateStateFromRemote
import com.spiderbiggen.manga.presentation.ui.manga.list.model.MangaScreenState
import com.spiderbiggen.manga.presentation.usecases.FormatAppError
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.time.Instant
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

private const val ACTIVE_FILTER_KEYS_KEY = "activeFilterKeys"

class MangaListViewModelTest {
    @Test
    fun `given unknown persisted keys when state is collected then all manga remain in source order`() =
        runBlocking {
            val source = listOf(overview("first"), overview("second", isRead = true))
            val savedStateHandle =
                SavedStateHandle(mapOf(ACTIVE_FILTER_KEYS_KEY to listOf("future")))

            val state =
                viewModel(savedStateHandle, source).state.first {
                    it.state is MangaScreenState.Ready
                }
            val readyState = state.state as MangaScreenState.Ready

            assertEquals(listOf("first", "second"), readyState.manga.map { it.id.value })
            assertEquals(emptyList<MangaFilter>(), state.activeFilters.toList())
        }

    @Test
    fun `given empty filters when state is collected then all manga remain in source order`() =
        runBlocking {
            val source =
                listOf(overview("first", isFavorite = true), overview("second", isRead = true))

            val state = viewModel(manga = source).state.first { it.state is MangaScreenState.Ready }
            val readyState = state.state as MangaScreenState.Ready

            assertEquals(listOf("first", "second"), readyState.manga.map { it.id.value })
        }

    @Test
    fun `given unread filter when state is collected then only unread manga remain`() =
        runBlocking {
            val source = listOf(overview("unread"), overview("read", isRead = true))
            val savedStateHandle =
                SavedStateHandle(mapOf(ACTIVE_FILTER_KEYS_KEY to listOf("unread")))

            val state =
                viewModel(savedStateHandle, source).state.first {
                    it.state is MangaScreenState.Ready
                }
            val readyState = state.state as MangaScreenState.Ready

            assertEquals(listOf("unread"), readyState.manga.map { it.id.value })
        }

    @Test
    fun `given favorites filter when state is collected then only favorite manga remain`() =
        runBlocking {
            val source = listOf(overview("favorite", isFavorite = true), overview("other"))
            val savedStateHandle =
                SavedStateHandle(mapOf(ACTIVE_FILTER_KEYS_KEY to listOf("favorites")))

            val state =
                viewModel(savedStateHandle, source).state.first {
                    it.state is MangaScreenState.Ready
                }
            val readyState = state.state as MangaScreenState.Ready

            assertEquals(listOf("favorite"), readyState.manga.map { it.id.value })
        }

    @Test
    fun `given unread and favorites filters when state is collected then predicates use AND semantics`() =
        runBlocking {
            val source =
                listOf(
                    overview("unread-favorite", isFavorite = true),
                    overview("read-favorite", isRead = true, isFavorite = true),
                    overview("unread-not-favorite"),
                )
            val savedStateHandle =
                SavedStateHandle(mapOf(ACTIVE_FILTER_KEYS_KEY to listOf("unread", "favorites")))

            val state =
                viewModel(savedStateHandle, source).state.first {
                    it.state is MangaScreenState.Ready
                }
            val readyState = state.state as MangaScreenState.Ready

            assertEquals(listOf("unread-favorite"), readyState.manga.map { it.id.value })
        }

    @Test
    fun `given persisted filters when set filter is repeated then keys are canonical and deterministic`() {
        val savedStateHandle =
            SavedStateHandle(
                mapOf(ACTIVE_FILTER_KEYS_KEY to listOf("favorites", "future", "unread", "unread"))
            )
        val viewModel = viewModel(savedStateHandle)

        viewModel.onAction(MangaListAction.SetFilter(MangaFilter.Favorites, enabled = true))
        viewModel.onAction(MangaListAction.SetFilter(MangaFilter.Unread, enabled = true))

        assertEquals(listOf("unread", "favorites"), savedStateHandle[ACTIVE_FILTER_KEYS_KEY])
    }

    @Test
    fun `given active filters when one filter is disabled then only remaining key is persisted`() {
        val savedStateHandle =
            SavedStateHandle(mapOf(ACTIVE_FILTER_KEYS_KEY to listOf("unread", "favorites")))
        val viewModel = viewModel(savedStateHandle)

        viewModel.onAction(MangaListAction.SetFilter(MangaFilter.Unread, enabled = false))

        assertEquals(listOf("favorites"), savedStateHandle[ACTIVE_FILTER_KEYS_KEY])
    }

    @Test
    fun `given active filters when clear action is dispatched then one empty collection is persisted`() {
        val savedStateHandle =
            SavedStateHandle(mapOf(ACTIVE_FILTER_KEYS_KEY to listOf("unread", "favorites")))
        val viewModel = viewModel(savedStateHandle)

        viewModel.onAction(MangaListAction.ClearFilters)

        assertEquals(emptyList<String>(), savedStateHandle[ACTIVE_FILTER_KEYS_KEY])
    }

    @Test
    fun `when refresh action is dispatched then remote update skips cache`() {
        val completed = CountDownLatch(1)
        var skipCache = false
        val viewModel =
            viewModel(
                onRefresh = { value ->
                    skipCache = value
                    completed.countDown()
                }
            )

        viewModel.onAction(MangaListAction.Refresh)

        assertTrue(completed.await(5, TimeUnit.SECONDS))
        assertTrue(skipCache)
    }

    @Test
    fun `when favorite action is dispatched then favorite use case receives manga id`() {
        val completed = CountDownLatch(1)
        val expectedId = MangaId("favorite")
        var actualId: MangaId? = null
        val viewModel =
            viewModel(
                onFavoriteClicked = { id ->
                    actualId = id
                    completed.countDown()
                }
            )

        viewModel.onAction(MangaListAction.FavoriteClicked(expectedId))

        assertTrue(completed.await(5, TimeUnit.SECONDS))
        assertEquals(expectedId, actualId)
    }

    private fun viewModel(
        savedStateHandle: SavedStateHandle = SavedStateHandle(),
        manga: List<MangaForOverview> = emptyList(),
        onFavoriteClicked: (MangaId) -> Unit = {},
        onRefresh: (Boolean) -> Unit = {},
    ) =
        MangaListViewModel(
            savedStateHandle = savedStateHandle,
            getOverviewManga = GetOverviewManga { flowOf(manga) },
            mapMangaListViewData = MapMangaListViewData(),
            toggleFavorite =
                ToggleFavorite { id ->
                    onFavoriteClicked(id)
                    false.right()
                },
            updateStateFromRemote =
                UpdateStateFromRemote { skipCache ->
                    onRefresh(skipCache)
                    Unit.right()
                },
            formatAppError = FormatAppError(),
        )

    private fun overview(
        id: String,
        isRead: Boolean = false,
        isFavorite: Boolean = false,
    ) =
        MangaForOverview(
            manga =
                Manga(
                    source = "test",
                    id = MangaId(id),
                    title = id,
                    coverImage = "",
                    dominantColor = null,
                    description = null,
                    status = "Ongoing",
                    updatedAt = Instant.parse("2024-01-01T00:00:00Z"),
                ),
            isFavorite = isFavorite,
            isRead = isRead,
            lastChapterId = ChapterId("chapter-$id"),
            readChapterCount = 0,
            totalChapterCount = 1,
        )
}
