package com.spiderbiggen.manga.presentation.ui.images

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.navigation.toRoute
import com.spiderbiggen.manga.domain.model.AppError
import com.spiderbiggen.manga.domain.model.Either
import com.spiderbiggen.manga.domain.model.SurroundingChapters
import com.spiderbiggen.manga.domain.model.andLeft
import com.spiderbiggen.manga.domain.model.id.ChapterId
import com.spiderbiggen.manga.domain.model.id.MangaId
import com.spiderbiggen.manga.domain.model.leftOr
import com.spiderbiggen.manga.domain.usecase.chapter.GetChapter
import com.spiderbiggen.manga.domain.usecase.chapter.GetChapterImages
import com.spiderbiggen.manga.domain.usecase.chapter.GetSurroundingChapters
import com.spiderbiggen.manga.domain.usecase.favorite.IsFavorite
import com.spiderbiggen.manga.domain.usecase.favorite.ToggleFavorite
import com.spiderbiggen.manga.domain.usecase.read.IsRead
import com.spiderbiggen.manga.domain.usecase.read.SetRead
import com.spiderbiggen.manga.domain.usecase.read.SetReadUpToChapter
import com.spiderbiggen.manga.presentation.extensions.defaultScope
import com.spiderbiggen.manga.presentation.ui.images.model.ImagesRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@HiltViewModel
class ImagesViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getChapter: GetChapter,
    private val getSurroundingChapters: GetSurroundingChapters,
    private val getChapterImages: GetChapterImages,
    private val isFavorite: IsFavorite,
    private val toggleFavorite: ToggleFavorite,
    private val isRead: IsRead,
    private val setRead: SetRead,
    private val setReadUpToChapter: SetReadUpToChapter,
) : ViewModel() {

    private val args = savedStateHandle.toRoute<ImagesRoute>()
    private val mangaId = MangaId(args.mangaId)
    private val chapterId = ChapterId(args.chapterId)

    private var surrounding = SurroundingChapters()

    private val mutableState = MutableStateFlow<ImagesScreenState>(ImagesScreenState.Loading)

    val state
        get() = mutableState.asStateFlow()

    suspend fun collect() {
        updateScreenState()
    }

    fun toggleFavorite() {
        defaultScope.launch {
            toggleFavorite(mangaId)
            updateScreenState()
        }
    }

    fun updateReadState() {
        defaultScope.launch {
            setRead(chapterId, true)
            updateScreenState()
        }
    }

    fun setReadUpToHere() {
        defaultScope.launch {
            setReadUpToChapter(chapterId)
            updateScreenState()
        }
    }

    private suspend fun updateScreenState() {
        withContext(Dispatchers.Default) {
            val deferredEitherChapter = async(Dispatchers.IO) { getChapter(chapterId) }
            val deferredEitherImages = async(Dispatchers.IO) { getChapterImages(chapterId) }
            val deferredSurrounding = async(Dispatchers.IO) { getSurroundingChapters(chapterId) }

            when (val data = deferredEitherChapter.await().andLeft(deferredEitherImages.await())) {
                is Either.Left -> {
                    val (chapter, images) = data.left
                    surrounding = deferredSurrounding.await().leftOr(surrounding)
                    val isFavorite = isFavorite(mangaId).leftOr(false)
                    val isRead = isRead(chapterId).leftOr(false)

                    mutableState.emit(
                        ImagesScreenState.Ready(
                            title = chapter.displayTitle(),
                            surrounding = surrounding,
                            isFavorite = isFavorite,
                            images = images.map { it.toExternalForm() }.toImmutableList(),
                            isRead = isRead,
                        ),
                    )
                }

                is Either.Right -> mutableState.emit(mapError(data.right))
            }
        }
    }

    private fun mapError(error: AppError): ImagesScreenState.Error {
        Log.e("ImagesViewModel", "failed to get images $error")
        return ImagesScreenState.Error("An error occurred")
    }
}
