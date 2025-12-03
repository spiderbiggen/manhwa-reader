package com.spiderbiggen.manga.presentation.ui.manga.list

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spiderbiggen.manga.domain.model.AppError
import com.spiderbiggen.manga.domain.model.Either
import com.spiderbiggen.manga.domain.model.id.MangaId
import com.spiderbiggen.manga.domain.model.leftOrElse
import com.spiderbiggen.manga.domain.model.manga.MangaForOverview
import com.spiderbiggen.manga.domain.usecase.favorite.ToggleFavorite
import com.spiderbiggen.manga.domain.usecase.manga.GetOverviewManga
import com.spiderbiggen.manga.domain.usecase.remote.UpdateMangaFromRemote
import com.spiderbiggen.manga.presentation.components.snackbar.SnackbarData
import com.spiderbiggen.manga.presentation.extensions.launchDefault
import com.spiderbiggen.manga.presentation.extensions.suspended
import com.spiderbiggen.manga.presentation.ui.manga.list.model.MangaScreenData
import com.spiderbiggen.manga.presentation.ui.manga.list.model.MangaScreenState
import com.spiderbiggen.manga.presentation.usecases.FormatAppError
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
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
    private val updateMangaFromRemote: UpdateMangaFromRemote,
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

    private val _state = MutableStateFlow(MangaScreenData())
    val state: StateFlow<MangaScreenData> = _state
        .onStart { onStart() }
        .stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(500),
            initialValue = _state.value,
        )

    suspend fun onStart() = launchDefault {
        updateMangas(skipCache = false)
        when (val result = getOverviewManga()) {
            is Either.Left -> mapSuccess(result.value)
            is Either.Right -> mapError(result)
        }
    }

    private suspend fun mapSuccess(flow: Flow<List<MangaForOverview>>) {
        val combinedFlows = combine(
            flow,
            unreadSelectedFlow,
            favoriteSelectedFlow,
        ) { manga, unreadSelected, favoriteSelected ->
            Triple(manga, unreadSelected, favoriteSelected)
        }

        combinedFlows.collectLatest { (mangaList, filterUnread, filterFavorites) ->
            val timeZone = TimeZone.currentSystemDefault()
            val filteredManga = mangaList
                .asSequence()
                .filter { !filterUnread || !it.isRead }
                .filter { !filterFavorites || it.isFavorite }
            val sectionedManga = splitMangasIntoSections(filteredManga, timeZone)
            val viewData = sectionedManga.map { (key, values) ->
                key to values.map { mapMangaListViewData(it, timeZone) }.toImmutableList()
            }

            _state.emit(
                MangaScreenData(
                    filterFavorites = filterFavorites,
                    filterUnread = filterUnread,
                    state = MangaScreenState.Ready(manga = viewData.toImmutableList()),
                ),
            )
        }
    }

    private suspend fun mapError(result: Either.Right<Flow<List<MangaForOverview>>, AppError>) {
        combine(
            favoriteSelectedFlow,
            unreadSelectedFlow,
        ) { (favorites, unread) -> unread to favorites }
            .collectLatest { (favorites, unread) ->
                _state.emit(
                    MangaScreenData(
                        filterFavorites = favorites,
                        filterUnread = unread,
                        state = mapError(result.value),
                    ),
                )
            }
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

    private fun mapError(error: AppError): MangaScreenState.Error {
        Log.e("MangaViewModel", "failed to get manga $error")
        return MangaScreenState.Error("An error occurred")
    }

    private suspend fun updateMangas(skipCache: Boolean) = coroutineScope {
        launch(viewModelScope.coroutineContext + Dispatchers.Default) {
            _isRefreshing.emit(true)
            updateMangaFromRemote(skipCache).leftOrElse {
                _snackbarFlow.emit(SnackbarData(formatAppError(it)))
            }
            yield()
            _isRefreshing.emit(false)
        }
    }
}
