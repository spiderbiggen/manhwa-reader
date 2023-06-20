package com.spiderbiggen.manhwa.presentation.ui.manhwa.favorites

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.spiderbiggen.manhwa.domain.model.AppError
import com.spiderbiggen.manhwa.domain.model.Either
import com.spiderbiggen.manhwa.domain.model.Manhwa
import com.spiderbiggen.manhwa.domain.usecase.manhwa.GetFavoriteManhwa
import com.spiderbiggen.manhwa.domain.usecase.manhwa.UpdateManhwa
import com.spiderbiggen.manhwa.presentation.model.ManhwaViewData
import com.spiderbiggen.manhwa.presentation.ui.manhwa.overview.ManhwaScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ManhwaFavoritesViewModel @Inject constructor(
    private val getFavoriteManhwa: GetFavoriteManhwa,
    private val updateManhwa: UpdateManhwa,
) : ViewModel() {

    private val mutableState =
        MutableStateFlow<ManhwaFavoritesScreenState>(ManhwaFavoritesScreenState.Loading)
    val state
        get() = mutableState.asStateFlow()

    var refreshing = mutableStateOf(false)

    suspend fun collect() {
        withContext(Dispatchers.IO) {
            updateScreenState()
        }
    }

    suspend fun onClickRefresh() {
        if (refreshing.value) return
        refreshing.value = true
        try {
            updateManhwa()
        } finally {
            refreshing.value = false
        }
    }

    private suspend fun updateScreenState() {
        when (val result = getFavoriteManhwa()) {
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
                        isFavorite = true,
                    )
                }
                mutableState.emit(ManhwaFavoritesScreenState.Ready(viewData))
            }
        }
    }

    private fun mapError(error: AppError): ManhwaFavoritesScreenState.Error {
        Log.e("ManhwaViewModel", "failed to get manhwa $error")
        return ManhwaFavoritesScreenState.Error("An error occurred")
    }
}