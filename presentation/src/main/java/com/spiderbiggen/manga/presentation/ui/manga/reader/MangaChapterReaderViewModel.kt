package com.spiderbiggen.manga.presentation.ui.manga.reader

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spiderbiggen.manga.domain.model.AppError
import com.spiderbiggen.manga.domain.model.Either
import com.spiderbiggen.manga.domain.model.andLeft
import com.spiderbiggen.manga.domain.model.chapter.SurroundingChapters
import com.spiderbiggen.manga.domain.model.leftOrElse
import com.spiderbiggen.manga.domain.usecase.chapter.GetChapter
import com.spiderbiggen.manga.domain.usecase.chapter.GetChapterImages
import com.spiderbiggen.manga.domain.usecase.chapter.GetSurroundingChapters
import com.spiderbiggen.manga.domain.usecase.favorite.IsFavoriteFlow
import com.spiderbiggen.manga.domain.usecase.favorite.ToggleFavorite
import com.spiderbiggen.manga.domain.usecase.read.SetRead
import com.spiderbiggen.manga.domain.usecase.read.SetReadUpToChapter
import com.spiderbiggen.manga.presentation.extensions.defaultScope
import com.spiderbiggen.manga.presentation.ui.manga.reader.navigation.MangaChapterReaderRoute
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = MangaChapterReaderViewModel.Factory::class)
class MangaChapterReaderViewModel @AssistedInject constructor(
    @Assisted navKey: MangaChapterReaderRoute,
    private val getChapter: GetChapter,
    private val getSurroundingChapters: GetSurroundingChapters,
    private val getChapterImages: GetChapterImages,
    private val isFavorite: IsFavoriteFlow,
    private val toggleFavorite: ToggleFavorite,
    private val setRead: SetRead,
    private val setReadUpToChapter: SetReadUpToChapter,
) : ViewModel() {

    private val mangaId = navKey.mangaId
    private val chapterId = navKey.chapterId

    private var surrounding = SurroundingChapters()

    private val mutableState = MutableStateFlow<MangaChapterReaderScreenState>(MangaChapterReaderScreenState.Loading)
    val state = mutableState.asStateFlow()
        .onStart { loadData() }
        .stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = mutableState.value,
        )

    suspend fun loadData() = coroutineScope {
        launch(viewModelScope.coroutineContext + Dispatchers.Main) {
            updateScreenState()
        }
    }

    private suspend fun updateScreenState() = coroutineScope {
        val deferredEitherImages = getChapterImages(chapterId)
        val deferredSurrounding = getSurroundingChapters(chapterId)

        val chapterFlow = getChapter(chapterId)
        val isFavoriteFlow = isFavorite(mangaId)

        val (images, surrounding) = deferredEitherImages
            .andLeft(deferredSurrounding)
            .leftOrElse {
                mutableState.emit(mapError(it))
                return@coroutineScope
            }

        this@MangaChapterReaderViewModel.surrounding = surrounding
        when (val data = chapterFlow.andLeft(isFavoriteFlow)) {
            is Either.Left -> {
                val (chapterFlow, isFavoriteFlow) = data.value
                val combinedFlows = combine(
                    chapterFlow,
                    isFavoriteFlow,
                ) { chapter, isFavorite ->
                    Triple(chapter.chapter, chapter.isRead, isFavorite)
                }

                combinedFlows.collect { (chapter, isRead, isFavorite) ->
                    mutableState.emit(
                        MangaChapterReaderScreenState.Ready(
                            title = chapter.displayTitle(),
                            surrounding = surrounding,
                            isFavorite = isFavorite,
                            images = images.map { it.toExternalForm() }.toImmutableList(),
                            isRead = isRead,
                        ),
                    )
                }
            }

            is Either.Right -> mutableState.emit(mapError(data.value))
        }
    }

    fun toggleFavorite() {
        defaultScope.launch {
            toggleFavorite(mangaId)
        }
    }

    fun updateReadState() {
        defaultScope.launch {
            setRead(chapterId, true)
        }
    }

    fun setReadUpToHere() {
        defaultScope.launch {
            setReadUpToChapter(chapterId)
        }
    }

    fun mapError(error: AppError): MangaChapterReaderScreenState.Error {
        Log.e("ImagesViewModel", "failed to get images $error")
        return MangaChapterReaderScreenState.Error("An error occurred")
    }

    @AssistedFactory
    fun interface Factory {
        fun create(navKey: MangaChapterReaderRoute): MangaChapterReaderViewModel
    }
}
