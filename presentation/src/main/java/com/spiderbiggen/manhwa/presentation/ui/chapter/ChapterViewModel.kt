package com.spiderbiggen.manhwa.presentation.ui.chapter

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spiderbiggen.manhwa.domain.model.AppError
import com.spiderbiggen.manhwa.domain.model.Either
import com.spiderbiggen.manhwa.domain.model.andLeft
import com.spiderbiggen.manhwa.domain.model.leftOr
import com.spiderbiggen.manhwa.domain.usecase.chapter.GetChapters
import com.spiderbiggen.manhwa.domain.usecase.favorite.IsFavorite
import com.spiderbiggen.manhwa.domain.usecase.favorite.ToggleFavorite
import com.spiderbiggen.manhwa.domain.usecase.manga.GetManga
import com.spiderbiggen.manhwa.domain.usecase.read.IsRead
import com.spiderbiggen.manhwa.domain.usecase.remote.UpdateChaptersFromRemote
import com.spiderbiggen.manhwa.presentation.ui.chapter.model.ChapterRowData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@HiltViewModel
class ChapterViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getChapters: GetChapters,
    private val getManga: GetManga,
    private val isFavorite: IsFavorite,
    private val isRead: IsRead,
    private val toggleFavorite: ToggleFavorite,
    private val updateChaptersFromRemote: UpdateChaptersFromRemote,
) : ViewModel() {

    private val mangaId: String = checkNotNull(savedStateHandle["mangaId"])

    private val mutableUpdatingState: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val refreshingState = mutableUpdatingState.asStateFlow()

    private val mutableScreenState =
        MutableStateFlow<ChapterScreenState>(ChapterScreenState.Loading)

    val state
        get() = mutableScreenState.asStateFlow()

    suspend fun collect() {
        withContext(Dispatchers.IO) {
            refresh(skipCache = false)
            updateScreenState()
        }
    }

    fun onClickRefresh() {
        refresh(skipCache = true)
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
                val (manga, chaptersFlow) = data.left
                mutableScreenState.emit(
                    ChapterScreenState.Ready(
                        manga = manga,
                        isFavorite = isFavorite(mangaId).leftOr(false),
                        chapters = emptyList(),
                    ),
                )

                chaptersFlow.collectLatest { list ->
                    val chapters = list.map {
                        ChapterRowData(
                            id = it.id,
                            title = it.displayTitle(),
                            date = it.date.toString(),
                            isRead = isRead(it.id).leftOr(false),
                        )
                    }
                    mutableScreenState.emit(
                        ChapterScreenState.Ready(
                            manga = manga,
                            isFavorite = isFavorite(mangaId).leftOr(false),
                            chapters = chapters,
                        ),
                    )
                }
            }

            is Either.Right -> mutableScreenState.emit(mapError(data.right))
        }
    }

    private fun mapError(error: AppError): ChapterScreenState.Error {
        Log.e("ChapterViewModel", "failed to get chapters $error")
        return ChapterScreenState.Error("An error occurred")
    }

    private fun refresh(skipCache: Boolean) {
        viewModelScope.launch {
            val minimumDelay = async {
                delay(500.milliseconds)
            }
            mutableUpdatingState.emit(true)
            updateChaptersFromRemote(mangaId, skipCache = skipCache)
            minimumDelay.await()
            // TODO show error notice (snackbar?)
            mutableUpdatingState.emit(false)
        }
    }
}