package com.spiderbiggen.manga.presentation.ui.manga.overview

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
import com.spiderbiggen.manga.presentation.extensions.defaultScope
import com.spiderbiggen.manga.presentation.ui.manga.model.MangaScreenData
import com.spiderbiggen.manga.presentation.ui.manga.model.MangaScreenState
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
class MangaOverviewViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val getOverviewManga: GetOverviewManga,
    private val mapMangaViewData: MapMangaViewData,
    private val splitMangasIntoSections: SplitMangasIntoSections,
    private val toggleFavorite: ToggleFavorite,
    private val updateMangaFromRemote: UpdateMangaFromRemote,
    private val formatAppError: FormatAppError,
) : ViewModel() {

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

    private val mutableUpdatingState: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val updatingState: StateFlow<Boolean> = mutableUpdatingState
        .onStart { updateMangas(skipCache = false) }
        .stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false,
        )

    private val mutableSnackbarFlow = MutableSharedFlow<SnackbarData>(1)
    val snackbarFlow: SharedFlow<SnackbarData>
        get() = mutableSnackbarFlow.asSharedFlow()

    private val updater = MutableSharedFlow<Unit>(1)

    private val mutableState = MutableStateFlow(MangaScreenData())
    val state: StateFlow<MangaScreenData> = mutableState
        .onStart { loadData() }
        .stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = mutableState.value,
        )

    suspend fun loadData() = coroutineScope {
        launch(viewModelScope.coroutineContext + Dispatchers.Default) {
            updateMangas(skipCache = false)
        }
        updater.emit(Unit)
        when (val result = getOverviewManga()) {
            is Either.Left -> mapSuccess(result.value)
            is Either.Right -> mapError(result)
        }
    }

    private suspend fun mapSuccess(flow: Flow<List<MangaForOverview>>) {
        val combinedFlows = combine(
            flow,
            updater,
            unreadSelectedFlow,
            favoriteSelectedFlow,
        ) { manga, _, unreadSelected, favoriteSelected ->
            Triple(manga, unreadSelected, favoriteSelected)
        }

        combinedFlows.collectLatest { (mangaList, filterUnread, filterFavorites) ->
            val timeZone = TimeZone.currentSystemDefault()
            val filteredManga = mangaList
                .asSequence()
                .filter { !filterUnread || !it.isRead }
                .filter { !filterFavorites || it.isFavorite }
                .toList()
            val sectionedManga = splitMangasIntoSections(filteredManga, timeZone)
            val viewData = sectionedManga.map { (key, values) ->
                key to values.map { mapMangaViewData(it, timeZone) }.toImmutableList()
            }

            mutableState.emit(
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
                mutableState.emit(
                    MangaScreenData(
                        filterFavorites = favorites,
                        filterUnread = unread,
                        state = mapError(result.value),
                    ),
                )
            }
    }

    private fun mapError(error: AppError): MangaScreenState.Error {
        Log.e("MangaViewModel", "failed to get manga $error")
        return MangaScreenState.Error("An error occurred")
    }

    fun onToggleUnread() {
        unreadSelected = !unreadSelected
    }

    fun onToggleFavorites() {
        favoriteSelected = !favoriteSelected
    }

    fun onPullToRefresh() {
        defaultScope.launch {
            updateMangas(skipCache = true)
        }
    }

    fun onClickFavorite(mangaId: MangaId) {
        defaultScope.launch {
            toggleFavorite(mangaId)
            updater.emit(Unit)
        }
    }

    private suspend fun updateMangas(skipCache: Boolean) = coroutineScope {
        launch(viewModelScope.coroutineContext + Dispatchers.Default) {
            mutableUpdatingState.emit(true)
            updateMangaFromRemote(skipCache).leftOrElse {
                mutableSnackbarFlow.emit(SnackbarData(formatAppError(it)))
            }
            yield()
            mutableUpdatingState.emit(false)
        }
    }
}
