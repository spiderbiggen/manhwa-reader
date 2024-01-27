package com.spiderbiggen.manhwa.presentation.ui.main

import androidx.lifecycle.ViewModel
import com.spiderbiggen.manhwa.domain.model.leftOr
import com.spiderbiggen.manhwa.domain.usecase.remote.GetUpdatingState
import com.spiderbiggen.manhwa.domain.usecase.remote.StartRemoteUpdate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val startRemoteUpdate: StartRemoteUpdate,
    private val getUpdatingState: GetUpdatingState,
): ViewModel() {

    val updatingState
        get() = getUpdatingState().map { it.leftOr(false) }

    fun onClickRefresh() {
        startRemoteUpdate(skipCache = true)
    }
}