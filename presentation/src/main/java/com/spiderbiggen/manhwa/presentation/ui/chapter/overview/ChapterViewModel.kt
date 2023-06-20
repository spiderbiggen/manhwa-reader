package com.spiderbiggen.manhwa.presentation.ui.chapter.overview

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.spiderbiggen.manhwa.domain.model.AppError
import com.spiderbiggen.manhwa.domain.model.Either
import com.spiderbiggen.manhwa.domain.model.andLeft
import com.spiderbiggen.manhwa.domain.usecase.chapter.GetChapters
import com.spiderbiggen.manhwa.domain.usecase.chapter.UpdateChapters
import com.spiderbiggen.manhwa.domain.usecase.manhwa.GetManhwa
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ChapterViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getManhwa: GetManhwa,
    private val getChapters: GetChapters,
    private val updateChapters: UpdateChapters,
) : ViewModel() {

    private val manhwaId: String = checkNotNull(savedStateHandle["manhwaId"])

    private val mutableScreenState = MutableStateFlow<ChapterScreenState>(ChapterScreenState.Loading)
    val state
        get() = mutableScreenState.asStateFlow()

    var refreshing = mutableStateOf(false)

    suspend fun collect() {
        runCatching { updateChapters(manhwaId) }
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

    private suspend fun updateScreenState() {
        withContext(Dispatchers.IO) {
            val eitherManhwa = getManhwa(manhwaId)
            val eitherChapters = getChapters(manhwaId)
            val data = eitherManhwa.andLeft(eitherChapters)
            when (data) {
                is Either.Left -> {
                    val (manhwa, chaptersFlow) = data.left
                    mutableScreenState.emit(ChapterScreenState.Ready(manhwa, emptyList()))
                    chaptersFlow.collectLatest {
                        mutableScreenState.emit(ChapterScreenState.Ready(manhwa, it))
                    }
                }

                is Either.Right -> mutableScreenState.emit(mapError(data.right))
            }
        }
    }


    private fun mapError(error: AppError): ChapterScreenState.Error =
        ChapterScreenState.Error("An error occurred")
}
