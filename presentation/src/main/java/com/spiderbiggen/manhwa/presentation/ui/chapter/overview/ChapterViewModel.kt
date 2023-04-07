package com.spiderbiggen.manhwa.presentation.ui.chapter.overview

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.spiderbiggen.manhwa.domain.repository.ManhwaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ChapterViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val manhwaRepository: ManhwaRepository,
) : ViewModel() {

    private val manhwaId: String = checkNotNull(savedStateHandle["manhwaId"])
    fun state() = manhwaRepository.getById(manhwaId)
}