package com.spiderbiggen.manhwa.presentation.ui.manga

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spiderbiggen.manhwa.domain.model.AppError
import com.spiderbiggen.manhwa.domain.model.Either
import com.spiderbiggen.manhwa.domain.model.Manga
import com.spiderbiggen.manhwa.domain.model.leftOr
import com.spiderbiggen.manhwa.domain.usecase.favorite.IsFavorite
import com.spiderbiggen.manhwa.domain.usecase.favorite.ToggleFavorite
import com.spiderbiggen.manhwa.domain.usecase.manga.GetActiveManga
import com.spiderbiggen.manhwa.domain.usecase.read.IsRead
import com.spiderbiggen.manhwa.domain.usecase.remote.UpdateMangaFromRemote
import com.spiderbiggen.manhwa.presentation.ui.manga.model.MangaViewData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class MangaViewModel @Inject constructor(
    private val getActiveManga: GetActiveManga,
    private val isFavorite: IsFavorite,
    private val isRead: IsRead,
    private val toggleFavorite: ToggleFavorite,
    private val updateMangaFromRemote: UpdateMangaFromRemote,
) : ViewModel() {

    private val mutableUpdatingState: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val updatingState = mutableUpdatingState.asStateFlow()

    private val updater = MutableSharedFlow<Unit>(1)
    private val mutableState = MutableStateFlow<MangaScreenState>(MangaScreenState.Loading)
    val state
        get() = mutableState.asStateFlow()

    private var favoritesOnly: Boolean = true
    private var unreadOnly: Boolean = false

    suspend fun collect() {
        withContext(Dispatchers.IO) {
            refresh(skipCache = false)
            updater.emit(Unit)
            updateScreenState()
        }
    }

    private suspend fun updateScreenState() {
        when (val result = getActiveManga()) {
            is Either.Left -> mapSuccess(result.left)
            is Either.Right -> mutableState.emit(mapError(result.right))
        }
    }

    private suspend fun mapSuccess(flow: Flow<List<Pair<Manga, String?>>>) {
        flow
            .combine(updater) { manga, _ -> manga }
            .collect { mangaList ->
                val timeZone = TimeZone.currentSystemDefault()
                val manga = mangaList.map { (manga, chapterId) ->
                    MangaViewData(
                        id = manga.id,
                        source = manga.source,
                        title = manga.title,
                        status = manga.status,
                        coverImage = manga.coverImage.toExternalForm(),
                        updatedAt = manga.updatedAt.toLocalDateTime(timeZone).date.toString(),
                        isFavorite = isFavorite(manga.id).leftOr(false),
                        readAll = chapterId?.let { isRead(it).leftOr(false) } == true,
                    )
                }
                val filtered = filterMangaViewData(manga)

                mutableState.emit(
                    MangaScreenState.Ready(
                        manga = filtered,
                        favoritesOnly = favoritesOnly,
                        unreadOnly = unreadOnly,
                    ),
                )
            }
    }

    private fun filterMangaViewData(entries: List<MangaViewData>): List<MangaViewData> {
        if (!favoritesOnly && !unreadOnly) return entries

        return entries.filter {
            (!favoritesOnly || it.isFavorite) && !(unreadOnly && it.readAll)
        }
    }

    private fun mapError(error: AppError): MangaScreenState.Error {
        Log.e("MangaViewModel", "failed to get manga $error")
        return MangaScreenState.Error("An error occurred")
    }

    fun onClickRefresh() {
        refresh(skipCache = true)
    }

    fun onClickFavorite(mangaId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            toggleFavorite(mangaId)
            updater.emit(Unit)
        }
    }

    fun toggleFavoritesOnly() {
        viewModelScope.launch(Dispatchers.IO) {
            favoritesOnly = !favoritesOnly
            updater.emit(Unit)
        }
    }

    fun toggleUnreadOnly() {
        viewModelScope.launch(Dispatchers.IO) {
            unreadOnly = !unreadOnly
            updater.emit(Unit)
        }
    }

    private fun refresh(skipCache: Boolean) {
        viewModelScope.launch {
            val minimumDelay = async {
                delay(500.milliseconds)
            }
            mutableUpdatingState.emit(true)
            updateMangaFromRemote(skipCache = skipCache)
            minimumDelay.await()
            // TODO show error notice (snackbar?)
            mutableUpdatingState.emit(false)
        }
    }
}
