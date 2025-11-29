package com.spiderbiggen.manga.presentation.ui.manga.chapter.list

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spiderbiggen.manga.domain.model.AppError
import com.spiderbiggen.manga.domain.model.Either
import com.spiderbiggen.manga.domain.model.andLeft
import com.spiderbiggen.manga.domain.model.leftOrElse
import com.spiderbiggen.manga.domain.usecase.chapter.GetOverviewChapters
import com.spiderbiggen.manga.domain.usecase.favorite.ToggleFavorite
import com.spiderbiggen.manga.domain.usecase.manga.GetManga
import com.spiderbiggen.manga.domain.usecase.remote.UpdateChaptersFromRemote
import com.spiderbiggen.manga.presentation.components.snackbar.SnackbarData
import com.spiderbiggen.manga.presentation.extensions.defaultScope
import com.spiderbiggen.manga.presentation.ui.manga.chapter.list.MangaChapterScreenState.Ready
import com.spiderbiggen.manga.presentation.ui.manga.chapter.list.navigation.MangaChapterListRoute
import com.spiderbiggen.manga.presentation.usecases.FormatAppError
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
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
    val isRefreshing = _isRefreshing
        .stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false,
        )

    private val _snackbarFlow = MutableSharedFlow<SnackbarData>(1)
    val snackbarFlow: SharedFlow<SnackbarData>
        get() = _snackbarFlow.asSharedFlow()

    private val mutableState = MutableStateFlow<MangaChapterScreenState>(MangaChapterScreenState.Loading)
    val state: StateFlow<MangaChapterScreenState> = mutableState
        .onStart { loadData() }
        .stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = MangaChapterScreenState.Loading,
        )

    private suspend fun loadData() = coroutineScope {
        launch(viewModelScope.coroutineContext + Dispatchers.Main) {
            updateChapters(skipCache = false)
            val eitherManga = getManga(mangaId)
            val eitherChapters = getOverviewChapters(mangaId)
            when (val data = eitherManga.andLeft(eitherChapters)) {
                is Either.Left -> {
                    val (mangaFlow, chaptersFlow) = data.value
                    val combinedFlows = combine(
                        mangaFlow,
                        chaptersFlow,
                    ) { manga, chapters ->
                        Triple(manga.manga, manga.isFavorite, chapters)
                    }

                    combinedFlows.collectLatest { (manga, isFavorite, chapters) ->
                        mutableState.emit(
                            Ready(
                                title = manga.title,
                                isFavorite = isFavorite,
                                chapters = chapters
                                    .map { mapChapterRowData(it) }
                                    .toImmutableList(),
                            ),
                        )
                    }
                }

                is Either.Right -> mutableState.emit(mapError(data.value))
            }
        }
    }

    fun onRefresh() {
        defaultScope.launch {
            updateChapters(skipCache = true)
        }
    }

    fun onToggleFavorite() {
        defaultScope.launch {
            toggleFavorite(mangaId)
        }
    }

    private fun mapError(error: AppError): MangaChapterScreenState.Error {
        Log.e("ChapterViewModel", "failed to get chapters $error")
        return MangaChapterScreenState.Error("An error occurred")
    }

    private suspend fun updateChapters(skipCache: Boolean) = coroutineScope {
        launch(viewModelScope.coroutineContext + Dispatchers.Default) {
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
