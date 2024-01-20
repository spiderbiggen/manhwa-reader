package com.spiderbiggen.manhwa.presentation.ui.manga

import android.util.Log
import androidx.lifecycle.ViewModel
import com.spiderbiggen.manhwa.domain.model.AppError
import com.spiderbiggen.manhwa.domain.model.Either
import com.spiderbiggen.manhwa.domain.model.Manga
import com.spiderbiggen.manhwa.domain.model.leftOr
import com.spiderbiggen.manhwa.domain.usecase.favorite.IsFavorite
import com.spiderbiggen.manhwa.domain.usecase.manga.GetActiveManga
import com.spiderbiggen.manhwa.domain.usecase.read.MangaIsRead
import com.spiderbiggen.manhwa.domain.usecase.remote.GetUpdatingState
import com.spiderbiggen.manhwa.domain.usecase.remote.StartRemoteUpdate
import com.spiderbiggen.manhwa.presentation.model.MangaViewData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import javax.inject.Inject

@HiltViewModel
class MangaViewModel @Inject constructor(
    private val getActiveManga: GetActiveManga,
    private val isFavorite: IsFavorite,
    private val mangaIsRead: MangaIsRead,
    private val startRemoteUpdate: StartRemoteUpdate,
    private val getUpdatingState: GetUpdatingState,
) : ViewModel() {

    private val mutableState = MutableStateFlow<MangaScreenState>(MangaScreenState.Loading)
    val state
        get() = mutableState.asStateFlow()

    val updatingState
        get() = getUpdatingState().map { it.leftOr(false) }

    suspend fun collect() {
        withContext(Dispatchers.IO) {
            updateScreenState()
        }
    }

    fun onClickRefresh() {
        startRemoteUpdate(skipCache = true)
    }

    private suspend fun updateScreenState() {
        when (val result = getActiveManga()) {
            is Either.Left -> mapSuccess(result.left)
            is Either.Right -> mutableState.emit(mapError(result.right))
        }
    }

    private suspend fun mapSuccess(flow: Flow<List<Manga>>) {
        flow.collectLatest { mangaList ->
            val timeZone = TimeZone.currentSystemDefault()
            val viewData = mangaList.map {
                MangaViewData(
                    id = it.id,
                    source = it.source,
                    title = it.title,
                    status = it.status,
                    coverImage = it.coverImage.toExternalForm(),
                    updatedAt = it.updatedAt.toLocalDateTime(timeZone).date.toString(),
                    isFavorite = isFavorite(it.id).leftOr(false),
                    readAll = mangaIsRead(it.id).leftOr(true),
                )
            }
            mutableState.emit(MangaScreenState.Ready(viewData))
        }
    }

    private fun mapError(error: AppError): MangaScreenState.Error {
        Log.e("MangaViewModel", "failed to get manga $error")
        return MangaScreenState.Error("An error occurred")
    }
}