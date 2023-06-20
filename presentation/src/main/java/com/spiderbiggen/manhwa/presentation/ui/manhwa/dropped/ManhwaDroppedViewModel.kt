package com.spiderbiggen.manhwa.presentation.ui.manhwa.dropped

import android.util.Log
import androidx.lifecycle.ViewModel
import com.spiderbiggen.manhwa.domain.model.AppError
import com.spiderbiggen.manhwa.domain.model.Either
import com.spiderbiggen.manhwa.domain.model.Manhwa
import com.spiderbiggen.manhwa.domain.model.leftOr
import com.spiderbiggen.manhwa.domain.usecase.favorite.IsFavorite
import com.spiderbiggen.manhwa.domain.usecase.manhwa.GetDroppedManhwa
import com.spiderbiggen.manhwa.presentation.model.ManhwaViewData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ManhwaDroppedViewModel @Inject constructor(
    private val getDroppedManhwa: GetDroppedManhwa,
    private val isFavorite: IsFavorite,
) : ViewModel() {

    private val mutableState =
        MutableStateFlow<ManhwaDroppedScreenState>(ManhwaDroppedScreenState.Loading)
    val state
        get() = mutableState.asStateFlow()

    suspend fun collect() {
        withContext(Dispatchers.IO) {
            updateScreenState()
        }
    }

    private suspend fun updateScreenState() {
        when (val result = getDroppedManhwa()) {
            is Either.Left -> mapSuccess(result.left)
            is Either.Right -> mutableState.emit(mapError(result.right))
        }
    }

    private suspend fun mapSuccess(flow: Flow<List<Manhwa>>) {
        withContext(Dispatchers.IO) {
            flow.collectLatest { manhwaList ->
                val viewData = manhwaList.sortedBy(Manhwa::title).map {
                    ManhwaViewData(
                        id = it.id,
                        source = it.source,
                        title = it.title,
                        status = it.status,
                        coverImage = it.coverImage.toExternalForm(),
                        updatedAt = it.updatedAt.toString(),
                        isFavorite = isFavorite(it.id).leftOr(false),
                    )
                }
                mutableState.emit(ManhwaDroppedScreenState.Ready(viewData))
            }
        }
    }

    private fun mapError(error: AppError): ManhwaDroppedScreenState.Error {
        Log.e("ManhwaViewModel", "failed to get manhwa $error")
        return ManhwaDroppedScreenState.Error("An error occurred")
    }
}