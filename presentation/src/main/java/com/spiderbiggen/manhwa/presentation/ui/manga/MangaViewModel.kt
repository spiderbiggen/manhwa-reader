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
import com.spiderbiggen.manhwa.presentation.ui.manga.model.MangaViewData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime
import javax.inject.Inject

@HiltViewModel
class MangaViewModel @Inject constructor(
    private val getActiveManga: GetActiveManga,
    private val isFavorite: IsFavorite,
    private val toggleFavorite: ToggleFavorite,
    private val isRead: IsRead,
) : ViewModel() {

    private val updater = MutableSharedFlow<Unit>(1)
    private val mutableState = MutableStateFlow<MangaScreenState>(MangaScreenState.Loading)
    val state
        get() = mutableState.asStateFlow()

    private var favoritesOnly: Boolean = true
    private var unreadOnly: Boolean = false

    suspend fun collect() {
        withContext(Dispatchers.IO) {
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
                val groups = groupManga(mangaList)
                val manga = groups.mapValues { (_, value) ->
                    value.map { (manga, chapterId) ->
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
                }
                val filtered = filterMangaViewData(manga)

                println("")

                mutableState.emit(
                    MangaScreenState.Ready(
                        manga = filtered,
                        favoritesOnly = favoritesOnly,
                        unreadOnly = unreadOnly,
                    )
                )
            }
    }

    private fun filterMangaViewData(manga: Map<String, List<MangaViewData>>): Map<String, List<MangaViewData>> {
        if (!favoritesOnly && !unreadOnly) return manga

        return manga.mapNotNull { (key, value) ->
            val newValues = value.filter {
                (!favoritesOnly || it.isFavorite) && !(unreadOnly && it.readAll)
            }
            if (newValues.isEmpty()) null else key to newValues
        }.toMap()
    }

    private fun mapError(error: AppError): MangaScreenState.Error {
        Log.e("MangaViewModel", "failed to get manga $error")
        return MangaScreenState.Error("An error occurred")
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

    private fun groupManga(value: List<Pair<Manga, String?>>): Map<String, List<Pair<Manga, String?>>> {
        val timeZone = TimeZone.currentSystemDefault()
        val now = Clock.System.now()
        val today = now.toLocalDateTime(timeZone).date
        val week = today.minus(1, DateTimeUnit.WEEK)
        val month = today.minus(today.dayOfMonth - 1, DateTimeUnit.DAY)
        return value.groupBy { (manga, _) ->
            val updatedAt = manga.updatedAt.toLocalDateTime(timeZone).date
            when {
                updatedAt >= today -> TODAY
                updatedAt >= week -> A_WEEK_AGO
                updatedAt >= month -> THIS_MONTH
                else -> updatedAt.year.toString()
            }
        }
    }

    private companion object {
        private const val TODAY = "Today"
        private const val A_WEEK_AGO = "This Week"
        private const val THIS_MONTH = "This Month"
    }
}