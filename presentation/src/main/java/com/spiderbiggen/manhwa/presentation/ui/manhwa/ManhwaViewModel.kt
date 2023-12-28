package com.spiderbiggen.manhwa.presentation.ui.manhwa

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.spiderbiggen.manhwa.domain.model.AppError
import com.spiderbiggen.manhwa.domain.model.Either
import com.spiderbiggen.manhwa.domain.model.Manhwa
import com.spiderbiggen.manhwa.domain.model.leftOr
import com.spiderbiggen.manhwa.domain.usecase.favorite.IsFavorite
import com.spiderbiggen.manhwa.domain.usecase.manhwa.GetActiveManhwa
import com.spiderbiggen.manhwa.domain.usecase.manhwa.UpdateManhwa
import com.spiderbiggen.manhwa.domain.usecase.read.IsManhwaRead
import com.spiderbiggen.manhwa.presentation.model.ManhwaViewData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.withContext
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaInstant
import kotlinx.datetime.toLocalDateTime
import javax.inject.Inject

@HiltViewModel
class ManhwaViewModel @Inject constructor(
    private val getActiveManhwa: GetActiveManhwa,
    private val isFavorite: IsFavorite,
    private val isManhwaRead: IsManhwaRead,
    private val updateManhwa: UpdateManhwa,
) : ViewModel() {

    private val mutableState = MutableStateFlow<ManhwaScreenState>(ManhwaScreenState.Loading)
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
        when (val result = getActiveManhwa()) {
            is Either.Left -> mapSuccess(result.left)
            is Either.Right -> mutableState.emit(mapError(result.right))
        }
    }

    private suspend fun mapSuccess(flow: Flow<List<Manhwa>>) {
        withContext(Dispatchers.IO) {
            flow.collectLatest { manhwaList ->
                val timeZone = TimeZone.currentSystemDefault()
                val viewData = manhwaList.map {
                    ManhwaViewData(
                        id = it.id,
                        source = it.source,
                        title = it.title,
                        status = it.status,
                        coverImage = it.coverImage.toExternalForm(),
                        updatedAt = it.updatedAt.toLocalDateTime(timeZone).date.toString(),
                        isFavorite = isFavorite(it.id).leftOr(false),
                        readAll = isManhwaRead(it.id).leftOr(true),
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