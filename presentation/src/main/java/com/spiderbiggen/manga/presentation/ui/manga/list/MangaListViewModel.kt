package com.spiderbiggen.manga.presentation.ui.manga.list

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.spiderbiggen.manga.domain.model.id.MangaId
import com.spiderbiggen.manga.domain.model.leftOrElse
import com.spiderbiggen.manga.domain.usecase.favorite.ToggleFavorite
import com.spiderbiggen.manga.domain.usecase.manga.GetOverviewManga
import com.spiderbiggen.manga.domain.usecase.remote.UpdateStateFromRemote
import com.spiderbiggen.manga.presentation.components.snackbar.SnackbarData
import com.spiderbiggen.manga.presentation.extensions.defaultScope
import com.spiderbiggen.manga.presentation.extensions.launchDefault
import com.spiderbiggen.manga.presentation.extensions.suspended
import com.spiderbiggen.manga.presentation.ui.manga.list.model.MangaScreenData
import com.spiderbiggen.manga.presentation.ui.manga.list.model.MangaScreenState
import com.spiderbiggen.manga.presentation.usecases.FormatAppError
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.yield
import kotlinx.datetime.TimeZone

private const val UNREAD_SELECTED_KEY = "unreadSelected"
private const val FAVORITE_SELECTED_KEY = "favoriteSelected"

@HiltViewModel
class MangaListViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val getOverviewManga: GetOverviewManga,
    private val mapMangaListViewData: MapMangaListViewData,
    private val splitMangasIntoSections: SplitMangasIntoSections,
    private val toggleFavorite: ToggleFavorite,
    private val updateStateFromRemote: UpdateStateFromRemote,
    private val formatAppError: FormatAppError,
) : ViewModel() {

    // TODO clean up search and/or filters
    private var unreadSelected: Boolean = savedStateHandle.get<Boolean>("unreadSelected") == true
        set(value) {
            field = value
            savedStateHandle.set(UNREAD_SELECTED_KEY, value)
        }
    private val unreadSelectedFlow: StateFlow<Boolean>
        get() = savedStateHandle.getStateFlow(UNREAD_SELECTED_KEY, unreadSelected)

    private var favoriteSelected: Boolean = savedStateHandle.get<Boolean>("favoriteSelected") == true
        set(value) {
            field = value
            savedStateHandle.set(FAVORITE_SELECTED_KEY, value)
        }
    private val favoriteSelectedFlow: StateFlow<Boolean>
        get() = savedStateHandle.getStateFlow(FAVORITE_SELECTED_KEY, favoriteSelected)

    private val _isRefreshing: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing

    private val _snackbarFlow = MutableSharedFlow<SnackbarData>(1)
    val snackbarFlow: SharedFlow<SnackbarData>
        get() = _snackbarFlow.asSharedFlow()

    val state: StateFlow<MangaScreenData> = screenStateFlow()
        .onStart { onStart() }
        .stateIn(
            defaultScope,
            started = SharingStarted.WhileSubscribed(500),
            initialValue = MangaScreenData(),
        )

    suspend fun onStart() = launchDefault {
        updateMangas(skipCache = false)
    }

    private fun screenStateFlow() = combine(
        getOverviewManga(),
        unreadSelectedFlow,
        favoriteSelectedFlow,
    ) { manga, unreadSelected, favoriteSelected ->
        val timeZone = TimeZone.currentSystemDefault()
        val filteredManga = manga
            .asSequence()
            .filter { !unreadSelected || !it.isRead }
            .filter { !favoriteSelected || it.isFavorite }
        val groupedManga = splitMangasIntoSections(filteredManga, timeZone)
        val viewData = groupedManga.map { (key, values) ->
            key to values.map { mapMangaListViewData(it, timeZone) }.toImmutableList()
        }

        MangaScreenData(
            filterFavorites = favoriteSelected,
            filterUnread = unreadSelected,
            state = MangaScreenState.Ready(manga = viewData.toImmutableList()),
        )
    }

    fun onToggleUnread() {
        unreadSelected = !unreadSelected
    }

    fun onToggleFavorites() {
        favoriteSelected = !favoriteSelected
    }

    fun onRefresh() = suspended {
        updateMangas(skipCache = true)
    }

    fun onFavoriteClick(mangaId: MangaId) = suspended {
        toggleFavorite(mangaId)
    }

    private suspend fun updateMangas(skipCache: Boolean) {
        _isRefreshing.emit(true)
        updateStateFromRemote(skipCache).leftOrElse {
            _snackbarFlow.emit(SnackbarData(formatAppError(it)))
        }
        yield()
        _isRefreshing.emit(false)
    }
}
