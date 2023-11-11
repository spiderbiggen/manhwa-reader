package com.spiderbiggen.manhwa.presentation.ui.manhwa.overview

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.spiderbiggen.manhwa.domain.model.AppError
import com.spiderbiggen.manhwa.domain.model.Either
import com.spiderbiggen.manhwa.domain.model.Manhwa
import com.spiderbiggen.manhwa.domain.model.leftOr
import com.spiderbiggen.manhwa.domain.usecase.favorite.IsFavorite
import com.spiderbiggen.manhwa.domain.usecase.manhwa.GetActiveManhwa
import com.spiderbiggen.manhwa.presentation.model.ManhwaViewData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ManhwaViewModel @Inject constructor(
    private val getActiveManhwa: GetActiveManhwa,
    private val isFavorite: IsFavorite,
) : ViewModel() {

    private val mutableState = MutableStateFlow<ManhwaScreenState>(ManhwaScreenState.Loading)
    val state
        get() = mutableState.asStateFlow()

    var refreshing = mutableStateOf(false)

    suspend fun collect() {
        withContext(Dispatchers.IO) {
            launch { onClickRefresh() }
            updateScreenState()
        }
    }

    suspend fun onClickRefresh() {
        if (refreshing.value) return
        refreshing.value = true
        refreshing.value = false
    }

    private suspend fun updateScreenState() {
        when (val result = getActiveManhwa()) {
            is Either.Left -> mapSuccess(result.left)
            is Either.Right -> mutableState.emit(mapError(result.right))
        }
    }

    private suspend fun mapSuccess(flow: Flow<List<Manhwa>>) {
        withContext(Dispatchers.IO) {
            flow.collectLatest { manhwaList ->
                val viewData = manhwaList.map {
                    ManhwaViewData(
                        id = it.id,
                        source = it.source,
                        title = it.title,
                        status = it.status,
                        coverImage = it.coverImage.toExternalForm(),
                        updatedAt = it.updatedAt.toString(),
                        isFavorite = isFavorite(it.id).leftOr(false),
                        readAll = false,
                    )
                }
                mutableState.emit(ManhwaScreenState.Ready(viewData))
            }
        }
    }

    private fun mapError(error: AppError): ManhwaScreenState.Error {
        Log.e("ManhwaViewModel", "failed to get manhwa $error")
        return ManhwaScreenState.Error("An error occurred")
    }
}