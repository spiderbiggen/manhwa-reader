package com.spiderbiggen.manhwa.presentation.ui.chapter.images

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.spiderbiggen.manhwa.domain.model.AppError
import com.spiderbiggen.manhwa.domain.model.Chapter
import com.spiderbiggen.manhwa.domain.model.Either
import com.spiderbiggen.manhwa.domain.model.SurroundingChapters
import com.spiderbiggen.manhwa.domain.model.leftOr
import com.spiderbiggen.manhwa.domain.usecase.chapter.GetChapter
import com.spiderbiggen.manhwa.domain.usecase.chapter.GetSurroundingChapters
import com.spiderbiggen.manhwa.domain.usecase.favorite.IsFavorite
import com.spiderbiggen.manhwa.domain.usecase.favorite.ToggleFavorite
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ImagesViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getChapter: GetChapter,
    private val getSurroundingChapters: GetSurroundingChapters,
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
            val eitherChapter = getChapter(manhwaId, chapterId)
            mutableState.emit(when (eitherChapter) {
                is Either.Left -> {
                    surrounding = getSurroundingChapters(manhwaId, chapterId).leftOr(surrounding)
                    val isFavorite = isFavorite(manhwaId).leftOr(false)

                    ImagesScreenState.Ready(
                        title = getTitle(eitherChapter.left),
                        surrounding = surrounding,
                        isFavorite = isFavorite,
                        images = eitherChapter.left.images.map { it.toExternalForm() }
                    )
                }

                is Either.Right -> mapError(eitherChapter.right)
            })
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