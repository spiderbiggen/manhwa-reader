package com.spiderbiggen.manga.presentation.ui.manga.chapter.overview

import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.spiderbiggen.manga.domain.model.AppError
import com.spiderbiggen.manga.domain.model.Either
import com.spiderbiggen.manga.domain.model.andLeft
import com.spiderbiggen.manga.domain.model.leftOr
import com.spiderbiggen.manga.domain.model.leftOrElse
import com.spiderbiggen.manga.domain.usecase.chapter.GetChapters
import com.spiderbiggen.manga.domain.usecase.favorite.IsFavorite
import com.spiderbiggen.manga.domain.usecase.favorite.ToggleFavorite
import com.spiderbiggen.manga.domain.usecase.manga.GetManga
import com.spiderbiggen.manga.domain.usecase.read.IsRead
import com.spiderbiggen.manga.domain.usecase.remote.UpdateChaptersFromRemote
import com.spiderbiggen.manga.presentation.components.snackbar.SnackbarData
import com.spiderbiggen.manga.presentation.extensions.defaultScope
import com.spiderbiggen.manga.presentation.ui.manga.chapter.overview.usecase.MapChapterRowData
import com.spiderbiggen.manga.presentation.ui.manga.model.MangaRoutes
import com.spiderbiggen.manga.presentation.usecases.FormatAppError
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.collections.immutable.persistentListOf
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
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield

@HiltViewModel
class ChapterViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getChapters: GetChapters,
    private val getManga: GetManga,
    private val isFavorite: IsFavorite,
    private val isRead: IsRead,
    private val toggleFavorite: ToggleFavorite,
    private val updateChaptersFromRemote: UpdateChaptersFromRemote,
    private val mapChapterRowData: MapChapterRowData,
    private val formatAppError: FormatAppError,
) : ViewModel() {

    private val args = savedStateHandle.toRoute<MangaRoutes.Chapters>()
    private val mangaId = args.mangaId

    private val mutableUpdatingState: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val refreshingState = mutableUpdatingState
        .onStart { updateChapters(skipCache = false) }
        .stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false,
        )

    private val mutableSnackbarFlow = MutableSharedFlow<SnackbarData>(1)
    val snackbarFlow: SharedFlow<SnackbarData>
        get() = mutableSnackbarFlow.asSharedFlow()

    private val mutableScreenState = MutableStateFlow<ChapterScreenState>(ChapterScreenState.Loading)
    val state: StateFlow<ChapterScreenState> = mutableScreenState
        .onStart { loadData() }
        .stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ChapterScreenState.Loading,
        )

    private suspend fun loadData() = coroutineScope {
        launch(viewModelScope.coroutineContext + Dispatchers.Main) {
            updateScreenState()
        }
    }

    fun onClickRefresh() {
        defaultScope.launch {
            updateChapters(skipCache = true)
        }
    }

    fun toggleFavorite() {
        toggleFavorite(mangaId)
        val state = mutableScreenState.value
        if (state is ChapterScreenState.Ready) {
            mutableScreenState.compareAndSet(state, state.copy(isFavorite = !state.isFavorite))
        }
    }

    private suspend fun updateScreenState() {
        val eitherManga = getManga(mangaId)
        val eitherChapters = getChapters(mangaId)
        when (val data = eitherManga.andLeft(eitherChapters)) {
            is Either.Left -> {
                val (manga, chaptersFlow) = data.value
                mutableScreenState.emit(
                    ChapterScreenState.Ready(
                        title = manga.title,
                        dominantColor = manga.dominantColor?.let { Color(it) },
                        isFavorite = isFavorite(mangaId).leftOr(false),
                        chapters = persistentListOf(),
                    ),
                )

                chaptersFlow.collectLatest { list ->
                    mutableScreenState.emit(
                        ChapterScreenState.Ready(
                            title = manga.title,
                            dominantColor = manga.dominantColor?.let { Color(it) },
                            isFavorite = isFavorite(mangaId).leftOr(false),
                            chapters = list
                                .map { mapChapterRowData(it, isRead(it.id).leftOr(false)) }
                                .toImmutableList(),
                        ),
                    )
                }
            }

            is Either.Right -> mutableScreenState.emit(mapError(data.value))
        }
    }

    private fun mapError(error: AppError): ChapterScreenState.Error {
        Log.e("ChapterViewModel", "failed to get chapters $error")
        return ChapterScreenState.Error("An error occurred")
    }

    private suspend fun updateChapters(skipCache: Boolean) = coroutineScope {
        launch(viewModelScope.coroutineContext + Dispatchers.Default) {
            mutableUpdatingState.emit(true)
            updateChaptersFromRemote(mangaId, skipCache = skipCache).leftOrElse {
                mutableSnackbarFlow.emit(SnackbarData(formatAppError(it)))
            }
            yield()
            mutableUpdatingState.emit(false)
        }
    }
}
