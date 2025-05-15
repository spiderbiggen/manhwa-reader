package com.spiderbiggen.manga.presentation.ui.manga.overview

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import com.spiderbiggen.manga.presentation.ui.manga.model.MangaScreenData
import com.spiderbiggen.manga.presentation.ui.manga.model.MangaScreenState
import com.spiderbiggen.manga.presentation.ui.manga.model.MangaViewData
import com.spiderbiggen.manga.presentation.usecases.FormatAppError
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@HiltViewModel
class MangaOverviewViewModel @Inject constructor(
    private val getActiveManga: GetActiveManga,
    private val isFavorite: IsFavorite,
    private val isRead: IsRead,
    private val toggleFavorite: ToggleFavorite,
    private val updateMangaFromRemote: UpdateMangaFromRemote,
    private val formatAppError: FormatAppError,
) : ViewModel() {

    private val mutableUnreadSelected = MutableStateFlow(false)
    private val mutableFavoriteSelected = MutableStateFlow(false)

    private val mutableUpdatingState: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val updatingState: StateFlow<Boolean> = mutableUpdatingState
        .onStart { updateMangas(skipCache = false) }
        .stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false,
        )

    private val mutableSnackbarFlow = MutableSharedFlow<SnackbarData>(1)
    val snackbarFlow: SharedFlow<SnackbarData>
        get() = mutableSnackbarFlow.asSharedFlow()

    private val updater = MutableSharedFlow<Unit>(1)

    private val mutableState = MutableStateFlow(MangaScreenData())
    val state: StateFlow<MangaScreenData> = mutableState
        .onStart { loadData() }
        .stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = mutableState.value,
        )

    suspend fun loadData() = coroutineScope {
        launch(viewModelScope.coroutineContext + Dispatchers.Default) {
            launch {
                updateMangas(skipCache = false)
            }
            updater.emit(Unit)
            when (val result = getActiveManga()) {
                is Either.Left -> mapSuccess(result.value)
                is Either.Right -> mapError(result)
            }
        }
    }

    private suspend fun mapSuccess(flow: Flow<List<Pair<Manga, ChapterId?>>>) {
        val combinedFlows = combine(
            flow,
            updater,
            mutableUnreadSelected,
            mutableFavoriteSelected,
        ) { manga, _, unreadSelected, favoriteSelected ->
            Triple(manga, unreadSelected, favoriteSelected)
        }

        combinedFlows.collectLatest { (mangaList, filterUnread, filterFavorites) ->
            val timeZone = TimeZone.Companion.currentSystemDefault()
            val manga = mangaList
                .asSequence()
                .map { (manga, chapterId) ->
                    Triple(
                        manga,
                        chapterId?.let { isRead(it).leftOr(false) } == true,
                        isFavorite(manga.id).leftOr(false),
                    )
                }
                .filter { (_, readAll, _) -> !filterUnread || !readAll }
                .filter { (_, _, favorite) -> !filterFavorites || favorite }
                .map { (manga, readAll, favorite) ->
                    MangaViewData(
                        id = manga.id,
                        source = manga.source,
                        title = manga.title,
                        status = manga.status,
                        coverImage = manga.coverImage.toExternalForm(),
                        updatedAt = manga.updatedAt.toLocalDateTime(timeZone).date.toString(),
                        isFavorite = favorite,
                        readAll = readAll,
                    )
                }

            mutableState.emit(
                MangaScreenData(
                    filterFavorites = filterFavorites,
                    filterUnread = filterUnread,
                    state = MangaScreenState.Ready(manga = manga.toImmutableList()),
                ),
            )
        }
    }


    private suspend fun mapError(result: Either.Right<Flow<List<Pair<Manga, ChapterId?>>>, AppError>) {
        combine(
            mutableFavoriteSelected,
            mutableUnreadSelected,
        ) { (favorites, unread) -> unread to favorites }
            .collectLatest { (favorites, unread) ->
                mutableState.emit(
                    MangaScreenData(
                        filterFavorites = favorites,
                        filterUnread = unread,
                        state = mapError(result.value),
                    ),
                )
            }
    }

    private fun mapError(error: AppError): MangaScreenState.Error {
        Log.e("MangaViewModel", "failed to get manga $error")
        return MangaScreenState.Error("An error occurred")
    }

    fun onToggleUnread() {
        defaultScope.launch {
            mutableUnreadSelected.emit(!mutableUnreadSelected.value)
        }
    }

    fun onToggleFavorites() {
        defaultScope.launch {
            mutableFavoriteSelected.emit(!mutableFavoriteSelected.value)
        }
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

    private suspend fun updateMangas(skipCache: Boolean) = coroutineScope {
        launch(viewModelScope.coroutineContext + Dispatchers.Default) {
            mutableUpdatingState.emit(true)
            updateMangaFromRemote(skipCache).leftOrElse {
                mutableSnackbarFlow.emit(SnackbarData(formatAppError(it)))
            }
            yield()
            mutableUpdatingState.emit(false)
        }
    }
}
