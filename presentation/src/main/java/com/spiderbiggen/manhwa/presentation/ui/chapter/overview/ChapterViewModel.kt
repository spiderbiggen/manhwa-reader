package com.spiderbiggen.manhwa.presentation.ui.chapter.overview

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spiderbiggen.manhwa.domain.model.AppError
import com.spiderbiggen.manhwa.domain.model.Either
import com.spiderbiggen.manhwa.domain.model.andLeft
import com.spiderbiggen.manhwa.domain.model.leftOr
import com.spiderbiggen.manhwa.domain.usecase.chapter.GetChapters
import com.spiderbiggen.manhwa.domain.usecase.chapter.UpdateChapters
import com.spiderbiggen.manhwa.domain.usecase.favorite.IsFavorite
import com.spiderbiggen.manhwa.domain.usecase.favorite.ToggleFavorite
import com.spiderbiggen.manhwa.domain.usecase.manhwa.GetManhwa
import com.spiderbiggen.manhwa.domain.usecase.read.IsRead
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ChapterViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getManhwa: GetManhwa,
    private val getChapters: GetChapters,
    private val updateChapters: UpdateChapters,
    private val isFavorite: IsFavorite,
    private val toggleFavorite: ToggleFavorite,
    private val isRead: IsRead,
) : ViewModel() {

    private val manhwaId: String = checkNotNull(savedStateHandle["manhwaId"])

    private val mutableScreenState =
        MutableStateFlow<ChapterScreenState>(ChapterScreenState.Loading)
    val state
        get() = mutableScreenState.asStateFlow()

    var refreshing = mutableStateOf(false)

    suspend fun collect() {
        viewModelScope.launch { updateChapters(manhwaId) }
        updateScreenState()
    }

    suspend fun onClickRefresh() {
        if (refreshing.value) return
        refreshing.value = true
        try {
            updateChapters(manhwaId)
        } finally {
            refreshing.value = false
        }
    }

    suspend fun toggleFavorite() {
        toggleFavorite(manhwaId)
        val state = mutableScreenState.value
        if (state is ChapterScreenState.Ready) {
            mutableScreenState.compareAndSet(state, state.copy(isFavorite = !state.isFavorite))
        }
    }

    private suspend fun updateScreenState() {
        withContext(Dispatchers.IO) {
            val eitherManhwa = getManhwa(manhwaId)
            val eitherChapters = getChapters(manhwaId)
            when (val data = eitherManhwa.andLeft(eitherChapters)) {
                is Either.Left -> {
                    val (manhwa, chaptersFlow) = data.left
                    mutableScreenState.emit(
                        ChapterScreenState.Ready(
                            manhwa = manhwa,
                            isFavorite = isFavorite(manhwaId).leftOr(false),
                            chapters = emptyList()
                        )
                    )

                    chaptersFlow.collectLatest { list ->
                        val chapters = list.map {
                            ChapterRowData(
                                chapter = it,
                                isRead = isRead(it.id).leftOr(false)
                            )
                        }
                        mutableScreenState.emit(
                            ChapterScreenState.Ready(
                                manhwa = manhwa,
                                isFavorite = isFavorite(manhwaId).leftOr(false),
                                chapters = chapters
                            )
                        )
                    }
                }

                is Either.Right -> mutableScreenState.emit(mapError(data.right))
            }
        }
    }


    private fun mapError(error: AppError): ChapterScreenState.Error =
        ChapterScreenState.Error("An error occurred")
}
