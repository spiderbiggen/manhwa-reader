package com.spiderbiggen.manhwa.presentation.ui.chapter.images

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.spiderbiggen.manhwa.domain.repository.FavoritesRepository
import com.spiderbiggen.manhwa.domain.repository.ManhwaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ImagesViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val manhwaRepository: ManhwaRepository,
    private val favoritesRepository: FavoritesRepository,
) : ViewModel() {

    private val manhwaId: String = checkNotNull(savedStateHandle["manhwaId"])
    private val chapterId: String = checkNotNull(savedStateHandle["chapterId"])
    private val title: String = getTitle()
    private var favorite: Boolean = favoritesRepository.isFavorite(manhwaId)
    private val previous: String?
    private val next: String?

    init {
        val surrounding = getSurroundingChapters()
        previous = surrounding.first
        next = surrounding.second
    }

    private val mutableState =
        MutableStateFlow<ImagesScreenState>(
            ImagesScreenState.Loading(
                title, previous, next, favorite
            )
        )
    val state
        get() = mutableState.asStateFlow()


    suspend fun collect() {
        withContext(Dispatchers.IO) {
            launch {
                val images = manhwaRepository.getChapterImages(chapterId)
                mutableState.emit(ImagesScreenState.Ready(title, previous, next, favorite, images))
            }
        }
    }

    fun toggleFavorite() {
        favorite = !favoritesRepository.isFavorite(manhwaId)
        favoritesRepository.setFavorite(manhwaId, favorite)
        mutableState.update {
            when (it) {
                is ImagesScreenState.Error -> it.copy(isFavorite = favorite)
                is ImagesScreenState.Loading -> it.copy(isFavorite = favorite)
                is ImagesScreenState.Ready -> it.copy(isFavorite = favorite)
            }
        }
    }

    private fun getSurroundingChapters(): Pair<String?, String?> {
        val chapters = manhwaRepository.getChapters(manhwaId)
        val index = chapters.indexOfFirst { it.id == chapterId }.takeUnless { it < 0 }
            ?: return null to null

        val previous = chapters.getOrNull(index + 1)
        val next = chapters.getOrNull(index - 1)
        return previous?.id to next?.id
    }

    private fun getTitle(): String = manhwaRepository.getChapterById(chapterId)?.let { item ->
        StringBuilder("Chapter ").apply {
            append(item.number)
            item.decimal?.let {
                append('.').append(it)
            }
            item.title?.let {
                append(" - ").append(it)
            }
        }.toString()
    } ?: "Chapter ?"
}