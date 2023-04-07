package com.spiderbiggen.manhwa.presentation.ui.manhwa.overview

import androidx.lifecycle.ViewModel
import com.spiderbiggen.manhwa.domain.repository.ManhwaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ManhwaViewModel @Inject constructor(
    private val manhwaRepository: ManhwaRepository,
) : ViewModel() {
    fun state() = manhwaRepository.getAll()
}