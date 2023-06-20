package com.spiderbiggen.manhwa.presentation.ui.chapter.images

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
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
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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
) : ViewModel() {

    private val manhwaId: String = checkNotNull(savedStateHandle["manhwaId"])
    private val chapterId: String = checkNotNull(savedStateHandle["chapterId"])
    private var surrounding = SurroundingChapters()


    private val mutableState = MutableStateFlow<ImagesScreenState>(ImagesScreenState.Loading)

    val state
        get() = mutableState.asStateFlow()


    suspend fun collect() {
        updateScreenState()
    }

    suspend fun toggleFavorite() {
        toggleFavorite(manhwaId)
        updateScreenState()
    }

    private suspend fun updateScreenState() {
        withContext(Dispatchers.IO) {
            val deferredEitherChapter = async { getChapter(chapterId) }
            val deferredEitherImages = async { getChapterImages(chapterId) }
            val data = deferredEitherChapter.await().andLeft(deferredEitherImages.await())
            when (data) {
                is Either.Left -> {
                    val (chapter, images) = data.left
                    surrounding = getSurroundingChapters(chapterId).leftOr(surrounding)
                    val isFavorite = isFavorite(manhwaId).leftOr(false)

                    mutableState.emit(ImagesScreenState.Ready(
                        title = getTitle(chapter),
                        surrounding = surrounding,
                        isFavorite = isFavorite,
                        images = images.map { it.toExternalForm() }
                    ))
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
                append(" - ").append(it)
            }
        }.toString()

    private fun mapError(error: AppError): ImagesScreenState.Error =
        ImagesScreenState.Error("An error occurred")

}