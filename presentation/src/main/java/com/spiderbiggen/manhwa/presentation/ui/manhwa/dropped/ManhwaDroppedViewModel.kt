package com.spiderbiggen.manhwa.presentation.ui.manhwa.dropped

import androidx.lifecycle.ViewModel
import com.spiderbiggen.manhwa.domain.model.Manhwa
import com.spiderbiggen.manhwa.domain.repository.FavoritesRepository
import com.spiderbiggen.manhwa.domain.repository.ManhwaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ManhwaDroppedViewModel @Inject constructor(
    private val manhwaRepository: ManhwaRepository,
    private val favoritesRepository: FavoritesRepository,
) : ViewModel() {

    private val mutableState = MutableStateFlow<ManhwaDroppedScreenState>(ManhwaDroppedScreenState.Loading)
    val state
        get() = mutableState.asStateFlow()

    suspend fun collect() {
        withContext(Dispatchers.IO) {
            manhwaRepository.flowAllManhwa().collectLatest {
                mutableState.emit(
                    ManhwaDroppedScreenState.Ready(
                        mapResponse(it),
                        favoritesRepository.favorites()
                    )
                )
            }
        }
    }

    private fun mapResponse(response: List<Manhwa>): List<Manhwa> =
        response.filter { it.status == "Dropped" }
}