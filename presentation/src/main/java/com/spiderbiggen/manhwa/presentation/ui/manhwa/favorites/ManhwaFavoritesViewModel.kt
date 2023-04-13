package com.spiderbiggen.manhwa.presentation.ui.manhwa.favorites

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
class ManhwaFavoritesViewModel @Inject constructor(
    private val manhwaRepository: ManhwaRepository,
    private val favoritesRepository: FavoritesRepository,
) : ViewModel() {

    private val mutableState = MutableStateFlow<ManhwaFavoritesScreenState>(ManhwaFavoritesScreenState.Loading)
    val state
        get() = mutableState.asStateFlow()

    suspend fun collect() {
        withContext(Dispatchers.IO) {
            manhwaRepository.flowAllManhwa().collectLatest {
                mutableState.emit(
                    ManhwaFavoritesScreenState.Ready(
                        mapResponse(it, favoritesRepository.favorites()),
                    )
                )
            }
        }
    }

    private fun mapResponse(response: List<Manhwa>, favorites: Set<String>): List<Manhwa> =
        response.filter { it.id in favorites }.sortedBy { it.status == "Dropped" }
}