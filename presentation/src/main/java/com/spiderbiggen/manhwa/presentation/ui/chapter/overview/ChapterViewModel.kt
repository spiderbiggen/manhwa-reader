package com.spiderbiggen.manhwa.presentation.ui.chapter.overview

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.spiderbiggen.manhwa.domain.model.AppError
import com.spiderbiggen.manhwa.domain.model.Either
import com.spiderbiggen.manhwa.domain.usecase.chapter.GetChapters
import com.spiderbiggen.manhwa.domain.usecase.manhwa.GetManhwa
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ChapterViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getManhwa: GetManhwa,
    private val getChapters: GetChapters,
) : ViewModel() {

    private val manhwaId: String = checkNotNull(savedStateHandle["manhwaId"])

    private val mutableState = MutableStateFlow<ChapterScreenState>(ChapterScreenState.Loading)
    val state
        get() = mutableState.asStateFlow()

    suspend fun collect() {
        updateScreenState()
    }

    private suspend fun updateScreenState() {
        withContext(Dispatchers.IO) {
            val deferredManhwa = async { getManhwa(manhwaId) }
            val deferredChapters = async { getChapters(manhwaId) }
            when (val eitherManhwa = deferredManhwa.await()) {
                is Either.Left -> {
                    mutableState.emit(ChapterScreenState.Ready(eitherManhwa.left, emptyList()))
                    when (val eitherChapters = deferredChapters.await()) {
                        is Either.Left -> mutableState.emit(
                            ChapterScreenState.Ready(
                                eitherManhwa.left,
                                eitherChapters.left
                            )
                        )

                        is Either.Right -> mutableState.emit(mapError(eitherChapters.right))
                    }

                }

                is Either.Right -> mutableState.emit(mapError(eitherManhwa.right))
            }
        }
    }


    private fun mapError(error: AppError): ChapterScreenState.Error =
        ChapterScreenState.Error("An error occurred")
}
