package com.spiderbiggen.manga.presentation.ui.manga.list

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.spiderbiggen.manga.domain.model.id.MangaId
import com.spiderbiggen.manga.domain.usecase.favorite.ToggleFavorite
import com.spiderbiggen.manga.domain.usecase.manga.GetOverviewManga
import com.spiderbiggen.manga.domain.usecase.remote.UpdateStateFromRemote
import com.spiderbiggen.manga.domain.usecase.update.CheckForUpdate
import com.spiderbiggen.manga.domain.usecase.update.DismissUpdateNotice
import com.spiderbiggen.manga.domain.usecase.update.ObserveUpdateNotice
import com.spiderbiggen.manga.presentation.components.snackbar.SnackbarData
import com.spiderbiggen.manga.presentation.extensions.defaultScope
import com.spiderbiggen.manga.presentation.extensions.launchDefault
import com.spiderbiggen.manga.presentation.extensions.suspended
import com.spiderbiggen.manga.presentation.ui.manga.list.model.MangaScreenData
import com.spiderbiggen.manga.presentation.ui.manga.list.model.MangaScreenState
import com.spiderbiggen.manga.presentation.ui.manga.list.model.toViewData
import com.spiderbiggen.manga.presentation.usecases.FormatAppError
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toPersistentSet
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.yield
import kotlinx.datetime.TimeZone

private const val ACTIVE_FILTER_KEYS_KEY = "activeFilterKeys"

class MangaListViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val getOverviewManga: GetOverviewManga,
    private val mapMangaListViewData: MapMangaListViewData,
    private val toggleFavorite: ToggleFavorite,
    private val updateStateFromRemote: UpdateStateFromRemote,
    private val observeUpdateNotice: ObserveUpdateNotice,
    private val dismissUpdateNotice: DismissUpdateNotice,
    checkForUpdate: CheckForUpdate,
    private val formatAppError: FormatAppError,
) : ViewModel() {

    init {
        // A failed check is not worth bothering the user with; the use case logs it and the check
        // is retried on the next launch.
        suspended { checkForUpdate() }
    }

    private val activeFilterKeysFlow: StateFlow<List<Any?>> =
        savedStateHandle.getStateFlow(ACTIVE_FILTER_KEYS_KEY, emptyList())

    private val activeFiltersFlow = activeFilterKeysFlow.map(::decodeFilterKeys)

    private val _isRefreshing: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing

    private val _snackbarFlow = MutableSharedFlow<SnackbarData>()
    val snackbarFlow: SharedFlow<SnackbarData>
        get() = _snackbarFlow.asSharedFlow()

    val state: StateFlow<MangaScreenData> =
        screenStateFlow()
            .onStart { onStart() }
            .stateIn(
                defaultScope,
                started = SharingStarted.WhileSubscribed(500),
                initialValue = MangaScreenData(),
            )

    private suspend fun onStart() = launchDefault {
        updateMangas(skipCache = false)
    }

    private fun screenStateFlow() =
        combine(
            getOverviewManga(),
            activeFiltersFlow,
            // Mapped before combining so the view data is not rebuilt on every manga emission.
            observeUpdateNotice().map { it?.toViewData() }.distinctUntilChanged(),
        ) { mangas, activeFilters, updateNotice ->
            val timeZone = TimeZone.currentSystemDefault()
            val viewData =
                mangas
                    .asSequence()
                    .filter { manga -> activeFilters.all { it.matches(manga) } }
                    .map { mapMangaListViewData(it, timeZone) }
                    .toImmutableList()

            MangaScreenData(
                activeFilters = activeFilters,
                state = MangaScreenState.Ready(viewData),
                updateNotice = updateNotice,
            )
        }

    fun onAction(action: MangaListAction) {
        when (action) {
            MangaListAction.Refresh -> onRefresh()
            is MangaListAction.SetFilter -> setFilter(action.filter, action.enabled)
            MangaListAction.ClearFilters -> clearFilters()
            MangaListAction.DismissUpdate -> onDismissUpdate()
            is MangaListAction.FavoriteClicked -> onFavoriteClick(action.id)
        }
    }

    private fun setFilter(filter: MangaFilter, enabled: Boolean) {
        val filters = decodeFilterKeys(activeFilterKeysFlow.value)
        val updatedFilters = if (enabled) filters.adding(filter) else filters.removing(filter)
        persistFilters(updatedFilters)
    }

    private fun clearFilters() {
        savedStateHandle[ACTIVE_FILTER_KEYS_KEY] = emptyList<String>()
    }

    private fun persistFilters(filters: ImmutableSet<MangaFilter>) {
        savedStateHandle[ACTIVE_FILTER_KEYS_KEY] =
            MangaFilter.entries.filter { it in filters }.map(MangaFilter::persistedKey)
    }

    private fun decodeFilterKeys(keys: List<Any?>) =
        keys
            .asSequence()
            .filterIsInstance<String>()
            .mapNotNull { key -> MangaFilter.entries.firstOrNull { it.persistedKey == key } }
            .toPersistentSet()

    private fun onRefresh() = suspended {
        updateMangas(skipCache = true)
    }

    private fun onDismissUpdate() = suspended {
        dismissUpdateNotice()
    }

    private fun onFavoriteClick(mangaId: MangaId) = suspended {
        toggleFavorite(mangaId)
    }

    private suspend fun updateMangas(skipCache: Boolean) {
        _isRefreshing.emit(true)
        updateStateFromRemote(skipCache).onLeft {
            _snackbarFlow.emit(SnackbarData(formatAppError(it)))
        }
        yield()
        _isRefreshing.emit(false)
    }
}
