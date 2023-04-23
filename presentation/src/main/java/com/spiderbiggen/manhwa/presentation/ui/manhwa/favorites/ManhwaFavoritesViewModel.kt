package com.spiderbiggen.manhwa.presentation.ui.manhwa.favorites

import androidx.lifecycle.ViewModel
import com.spiderbiggen.manhwa.domain.model.AppError
import com.spiderbiggen.manhwa.domain.model.Either
import com.spiderbiggen.manhwa.domain.model.Manhwa
import com.spiderbiggen.manhwa.domain.usecase.manhwa.GetFavoriteManhwa
import com.spiderbiggen.manhwa.presentation.model.ManhwaViewData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ManhwaFavoritesViewModel @Inject constructor(
    private val getFavoriteManhwa: GetFavoriteManhwa,
) : ViewModel() {

    private val mutableState =
        MutableStateFlow<ManhwaFavoritesScreenState>(ManhwaFavoritesScreenState.Loading)
    val state
        get() = mutableState.asStateFlow()

    suspend fun collect() {
        withContext(Dispatchers.IO) {
            updateScreenState()
        }
    }

    private suspend fun updateScreenState() {
        mutableState.emit(
            when (val result = getFavoriteManhwa()) {
                is Either.Left -> mapSuccess(result.left)
                is Either.Right -> mapError(result.right)
            }
        )
    }

    private suspend fun mapSuccess(manhwaList: List<Manhwa>): ManhwaFavoritesScreenState.Ready {
        return withContext(Dispatchers.IO) {
            val viewData = manhwaList.sortedByDescending(Manhwa::updatedAt)
                .map {
                    ManhwaViewData(
                        id = it.id,
                        source = it.source,
                        title = it.title,
                        status = it.status,
                        coverImage = it.coverImage.toExternalForm(),
                        updatedAt = it.updatedAt.toString(),
                        isFavorite = true,
                    )
                }
            ManhwaFavoritesScreenState.Ready(viewData)
        }
    }

    private fun mapError(error: AppError): ManhwaFavoritesScreenState.Error =
        ManhwaFavoritesScreenState.Error("An error occurred")
}