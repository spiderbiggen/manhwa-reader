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
    private var favorite: Boolean = favoritesRepository.isFavorite(manhwaId)
    private lateinit var surrounding: SurroundingChapters
    private val previous: String?
        get() = surrounding.previous
    private val next: String?
        get() = surrounding.next


    init {
        updateSurroundingChapters()
    }

    private val mutableState by lazy {
        MutableStateFlow<ImagesScreenState>(
            ImagesScreenState.Loading(getTitle(), previous, next, favorite)
        )
    }

    val state
        get() = mutableState.asStateFlow()


    suspend fun collect() {
        withContext(Dispatchers.IO) {
            launch {
                val images = manhwaRepository.getChapter(manhwaId, chapterId)?.images.orEmpty()
                updateSurroundingChapters()
                mutableState.emit(
                    ImagesScreenState.Ready(
                        title = getTitle(),
                        previous = previous,
                        next = next,
                        isFavorite = favorite,
                        images = images
                    )
                )
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

    private fun updateSurroundingChapters() {
        val chapters = manhwaRepository.getCachedChapters(manhwaId)
        val index = chapters.indexOfFirst { it.id == chapterId }
        if (index < 0) {
            surrounding = SurroundingChapters()
        }

        val previous = chapters.getOrNull(index + 1)
        val next = chapters.getOrNull(index - 1)
        surrounding = SurroundingChapters(previous?.id, next?.id)
    }

    private fun getTitle(): String = manhwaRepository.getCachedChapter(chapterId)?.let { item ->
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

    private data class SurroundingChapters(val previous: String? = null, val next: String? = null)
}