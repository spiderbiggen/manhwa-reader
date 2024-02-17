package com.spiderbiggen.manhwa.presentation.ui.chapter.images

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spiderbiggen.manhwa.domain.model.AppError
import com.spiderbiggen.manhwa.domain.model.Chapter
import com.spiderbiggen.manhwa.domain.model.Either
import com.spiderbiggen.manhwa.domain.model.SurroundingChapters
import com.spiderbiggen.manhwa.domain.model.andLeft
import com.spiderbiggen.manhwa.domain.model.leftOr
import com.spiderbiggen.manhwa.domain.usecase.chapter.GetChapter
import com.spiderbiggen.manhwa.domain.usecase.chapter.GetChapterImages
import com.spiderbiggen.manhwa.domain.usecase.chapter.GetSurroundingChapters
import com.spiderbiggen.manhwa.domain.usecase.favorite.IsFavorite
import com.spiderbiggen.manhwa.domain.usecase.favorite.ToggleFavorite
import com.spiderbiggen.manhwa.domain.usecase.read.IsRead
import com.spiderbiggen.manhwa.domain.usecase.read.SetRead
import com.spiderbiggen.manhwa.domain.usecase.read.SetReadUpToChapter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

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
    private val setReadUpToChapter: SetReadUpToChapter
) : ViewModel() {

    private val mangaId: String = checkNotNull(savedStateHandle["mangaId"])
    private val chapterId: String = checkNotNull(savedStateHandle["chapterId"])
    private var surrounding = SurroundingChapters()


    private val mutableState = MutableStateFlow<ImagesScreenState>(ImagesScreenState.Loading)

    val state
        get() = mutableState.asStateFlow()


    suspend fun collect() {
        updateScreenState()
    }

    fun toggleFavorite() {
        viewModelScope.launch {
            toggleFavorite(mangaId)
            updateScreenState()
        }
    }

    fun updateReadState() {
        viewModelScope.launch {
            setRead(chapterId, true)
            updateScreenState()
        }
    }

    fun setReadUpToHere() {
        viewModelScope.launch {
            setReadUpToChapter(chapterId)
            updateScreenState()
        }
    }

    private suspend fun updateScreenState() {
        withContext(Dispatchers.IO) {
            val deferredEitherChapter = async { getChapter(chapterId) }
            val deferredEitherImages = async { getChapterImages(chapterId) }
            when (val data = deferredEitherChapter.await().andLeft(deferredEitherImages.await())) {
                is Either.Left -> {
                    val (chapter, images) = data.left
                    surrounding = getSurroundingChapters(chapterId).leftOr(surrounding)
                    val isFavorite = isFavorite(mangaId).leftOr(false)
                    val isRead = isRead(chapterId).leftOr(false)

                    mutableState.emit(
                        ImagesScreenState.Ready(
                            title = getTitle(chapter),
                            surrounding = surrounding,
                            isFavorite = isFavorite,
                            images = images.map { it.toExternalForm() },
                            isRead = isRead,
                        )
                    )
                }

                is Either.Right -> mutableState.emit(mapError(data.right))
            }
        }
    }

    private fun getTitle(chapter: Chapter): String =
        StringBuilder("Chapter ").apply {
            append(chapter.number)
            chapter.decimal?.let {
                append('.').append(it)
            }
            chapter.title?.let {
                if (it[0].isLetterOrDigit()) append(" - ")
                append(it)
            }
        }.toString()

    private fun mapError(error: AppError): ImagesScreenState.Error {
        Log.e("ImagesViewModel", "failed to get images $error")
        return ImagesScreenState.Error("An error occurred")
    }

}