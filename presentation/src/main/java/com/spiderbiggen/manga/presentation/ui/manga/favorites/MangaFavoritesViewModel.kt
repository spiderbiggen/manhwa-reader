package com.spiderbiggen.manga.presentation.ui.manga.favorites

import android.util.Log
import androidx.lifecycle.ViewModel
import com.spiderbiggen.manga.domain.model.AppError
import com.spiderbiggen.manga.domain.model.Either
import com.spiderbiggen.manga.domain.model.Manga
import com.spiderbiggen.manga.domain.model.id.ChapterId
import com.spiderbiggen.manga.domain.model.id.MangaId
import com.spiderbiggen.manga.domain.model.leftOr
import com.spiderbiggen.manga.domain.model.leftOrElse
import com.spiderbiggen.manga.domain.usecase.favorite.IsFavorite
import com.spiderbiggen.manga.domain.usecase.favorite.ToggleFavorite
import com.spiderbiggen.manga.domain.usecase.manga.GetActiveManga
import com.spiderbiggen.manga.domain.usecase.read.IsRead
import com.spiderbiggen.manga.domain.usecase.remote.UpdateMangaFromRemote
import com.spiderbiggen.manga.presentation.components.snackbar.SnackbarData
import com.spiderbiggen.manga.presentation.extensions.defaultScope
import com.spiderbiggen.manga.presentation.ui.manga.model.MangaScreenState
import com.spiderbiggen.manga.presentation.ui.manga.model.MangaViewData
import com.spiderbiggen.manga.presentation.usecases.FormatAppError
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@HiltViewModel
class MangaFavoritesViewModel @Inject constructor(
    private val getActiveManga: GetActiveManga,
    private val isFavorite: IsFavorite,
    private val isRead: IsRead,
    private val toggleFavorite: ToggleFavorite,
    private val updateMangaFromRemote: UpdateMangaFromRemote,
    private val formatAppError: FormatAppError,
) : ViewModel() {

    private val mutableUpdatingState: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val updatingState = mutableUpdatingState.asStateFlow()

    private val mutableSnackbarFlow = MutableSharedFlow<SnackbarData>(1)
    val snackbarFlow: SharedFlow<SnackbarData>
        get() = mutableSnackbarFlow.asSharedFlow()

    private val updater = MutableSharedFlow<Unit>(1)
    private val mutableState = MutableStateFlow<MangaScreenState>(MangaScreenState.Loading)
    val state: StateFlow<MangaScreenState>
        get() = mutableState.asStateFlow()

    suspend fun collect() {
        withContext(Dispatchers.Default) {
            launch {
                updateMangas(skipCache = false)
            }
            updater.emit(Unit)
            updateScreenState()
        }
    }

    private suspend fun updateScreenState() {
        when (val result = getActiveManga()) {
            is Either.Left -> mapSuccess(result.value)
            is Either.Right -> mutableState.emit(mapError(result.value))
        }
    }

    private suspend fun mapSuccess(flow: Flow<List<Pair<Manga, ChapterId?>>>) {
        flow
            .combine(updater) { manga, _ -> manga }
            .collect { mangaList ->
                val timeZone = TimeZone.currentSystemDefault()
                val manga = mangaList.asSequence()
                    .filter { (manga, _) -> isFavorite(manga.id).leftOr(false) }
                    .map { (manga, chapterId) ->
                        MangaViewData(
                            id = manga.id,
                            source = manga.source,
                            title = manga.title,
                            status = manga.status,
                            coverImage = manga.coverImage.toExternalForm(),
                            updatedAt = manga.updatedAt.toLocalDateTime(timeZone).date.toString(),
                            isFavorite = true,
                            readAll = chapterId?.let { isRead(it).leftOr(false) } == true,
                        )
                    }

                mutableState.emit(MangaScreenState.Ready(manga = manga.toImmutableList()))
            }
    }

    private fun mapError(error: AppError): MangaScreenState.Error {
        Log.e("MangaViewModel", "failed to get manga $error")
        return MangaScreenState.Error("An error occurred")
    }

    fun onPullToRefresh() {
        defaultScope.launch {
            updateMangas(skipCache = true)
        }
    }

    fun onClickFavorite(mangaId: MangaId) {
        defaultScope.launch {
            toggleFavorite(mangaId)
            updater.emit(Unit)
        }
    }

    private suspend fun updateMangas(skipCache: Boolean) {
        mutableUpdatingState.emit(true)
        updateMangaFromRemote(skipCache).leftOrElse {
            mutableSnackbarFlow.emit(SnackbarData(formatAppError(it)))
        }
        yield()
        mutableUpdatingState.emit(false)
    }
}
