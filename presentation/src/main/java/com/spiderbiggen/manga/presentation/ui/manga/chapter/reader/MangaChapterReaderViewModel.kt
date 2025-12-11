package com.spiderbiggen.manga.presentation.ui.manga.chapter.reader

import android.util.Log
import androidx.lifecycle.ViewModel
import com.spiderbiggen.manga.domain.model.Either
import com.spiderbiggen.manga.domain.model.chapter.SurroundingChapters
import com.spiderbiggen.manga.domain.usecase.chapter.GetChapter
import com.spiderbiggen.manga.domain.usecase.chapter.GetChapterImages
import com.spiderbiggen.manga.domain.usecase.chapter.GetSurroundingChapters
import com.spiderbiggen.manga.domain.usecase.favorite.IsFavoriteFlow
import com.spiderbiggen.manga.domain.usecase.favorite.ToggleFavorite
import com.spiderbiggen.manga.domain.usecase.read.SetRead
import com.spiderbiggen.manga.domain.usecase.read.SetReadUpToChapter
import com.spiderbiggen.manga.presentation.extensions.defaultScope
import com.spiderbiggen.manga.presentation.extensions.launchDefault
import com.spiderbiggen.manga.presentation.extensions.suspended
import com.spiderbiggen.manga.presentation.ui.manga.chapter.reader.navigation.MangaChapterReaderRoute
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn

private const val TAG = "MangaChapterReaderViewModel"

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

    private val surroundingChapters = MutableStateFlow(SurroundingChapters())
    private val chapterImages = MutableStateFlow<ImmutableList<String>>(persistentListOf())

    val state: StateFlow<MangaChapterReaderScreenState> = screenStateFlow()
        .onStart { loadData() }
        .stateIn(
            defaultScope,
            started = SharingStarted.WhileSubscribed(500),
            initialValue = MangaChapterReaderScreenState.Loading,
        )

    suspend fun loadData() {
        launchDefault {
            when (val images = getChapterImages(chapterId)) {
                is Either.Left -> chapterImages.emit(images.value)
                is Either.Right -> Log.e(TAG, "failed to get images")
            }
        }
        launchDefault {
            when (val surrounding = getSurroundingChapters(chapterId)) {
                is Either.Left -> surroundingChapters.emit(surrounding.value)
                is Either.Right -> Log.e(TAG, "failed to get surrounding chapters")
            }
        }
    }

    private fun screenStateFlow() = combine(
        getChapter(chapterId),
        isFavorite(mangaId),
        surroundingChapters,
        chapterImages,
    ) { chapterState, isFavorite, surrounding, images ->
        MangaChapterReaderScreenState.Ready(
            title = chapterState?.chapter?.displayTitle(),
            surrounding = surrounding,
            isFavorite = isFavorite,
            images = images,
            isRead = chapterState?.isRead == true,
        )
    }

    fun toggleFavorite() = suspended {
        toggleFavorite(mangaId)
    }

    fun updateReadState() = suspended {
        setRead(chapterId, true)
    }

    fun setReadUpToHere() = suspended {
        setReadUpToChapter(chapterId)
    }

    @AssistedFactory
    fun interface Factory {
        fun create(navKey: MangaChapterReaderRoute): MangaChapterReaderViewModel
    }
}
