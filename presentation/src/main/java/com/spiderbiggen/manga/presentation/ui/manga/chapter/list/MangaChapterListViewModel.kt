package com.spiderbiggen.manga.presentation.ui.manga.chapter.list

import androidx.lifecycle.ViewModel
import com.spiderbiggen.manga.domain.model.leftOrElse
import com.spiderbiggen.manga.domain.usecase.chapter.GetOverviewChapters
import com.spiderbiggen.manga.domain.usecase.favorite.ToggleFavorite
import com.spiderbiggen.manga.domain.usecase.manga.GetManga
import com.spiderbiggen.manga.domain.usecase.remote.UpdateChaptersFromRemote
import com.spiderbiggen.manga.presentation.components.snackbar.SnackbarData
import com.spiderbiggen.manga.presentation.extensions.defaultContext
import com.spiderbiggen.manga.presentation.extensions.defaultScope
import com.spiderbiggen.manga.presentation.extensions.launchDefault
import com.spiderbiggen.manga.presentation.extensions.suspended
import com.spiderbiggen.manga.presentation.ui.manga.chapter.list.MangaChapterScreenState.Ready
import com.spiderbiggen.manga.presentation.ui.manga.chapter.list.navigation.MangaChapterListRoute
import com.spiderbiggen.manga.presentation.usecases.FormatAppError
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield

@HiltViewModel(assistedFactory = MangaChapterListViewModel.Factory::class)
class MangaChapterListViewModel @AssistedInject constructor(
    @Assisted navKey: MangaChapterListRoute,
    private val getOverviewChapters: GetOverviewChapters,
    private val getManga: GetManga,
    private val toggleFavorite: ToggleFavorite,
    private val updateChaptersFromRemote: UpdateChaptersFromRemote,
    private val mapChapterRowData: MapChapterRowData,
    private val formatAppError: FormatAppError,
) : ViewModel() {

    private val mangaId = navKey.id

    private val _isRefreshing: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing

    private val _snackbarFlow = MutableSharedFlow<SnackbarData>(1)
    val snackbarFlow: SharedFlow<SnackbarData>
        get() = _snackbarFlow.asSharedFlow()

    val state: StateFlow<MangaChapterScreenState> = screenStateFlow()
        .onStart { onStart() }
        .stateIn(
            defaultScope,
            started = SharingStarted.WhileSubscribed(500),
            initialValue = MangaChapterScreenState.Loading,
        )

    private suspend fun onStart() = launchDefault {
        updateChapters(skipCache = false)
    }

    private fun screenStateFlow() = combine(
        getManga(mangaId),
        getOverviewChapters(mangaId),
    ) { manga, chapters ->
        Ready(
            title = manga?.manga?.title,
            isFavorite = manga?.isFavorite == true,
            chapters = chapters
                .map { mapChapterRowData(it) }
                .toImmutableList(),
        )
    }

    fun onRefresh() = suspended {
        updateChapters(skipCache = true)
    }

    fun onToggleFavorite() = suspended {
        toggleFavorite(mangaId)
    }

    private suspend fun updateChapters(skipCache: Boolean) = coroutineScope {
        launch(defaultContext) {
            _isRefreshing.emit(true)
            updateChaptersFromRemote(mangaId, skipCache = skipCache).leftOrElse {
                _snackbarFlow.emit(SnackbarData(formatAppError(it)))
            }
            yield()
            _isRefreshing.emit(false)
        }
    }

    @AssistedFactory
    fun interface Factory {
        fun create(navKey: MangaChapterListRoute): MangaChapterListViewModel
    }
}
