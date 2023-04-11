package com.spiderbiggen.manhwa.presentation.ui.chapter.overview

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.spiderbiggen.manhwa.domain.repository.ManhwaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ChapterViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val manhwaRepository: ManhwaRepository,
) : ViewModel() {

    private val manhwaId: String = checkNotNull(savedStateHandle["manhwaId"])
    private val mutableState = MutableStateFlow<ChapterScreenState>(ChapterScreenState.Loading)
    val state
        get() = mutableState.asStateFlow()

    suspend fun collect() {
        withContext(Dispatchers.IO) {
            manhwaRepository.getSingleFlow(manhwaId).collect { (manhwa, list) ->
                mutableState.emit(ChapterScreenState.Ready(manhwa, list))
            }
        }
    }
}
