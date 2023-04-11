package com.spiderbiggen.manhwa.presentation.ui.manhwa.overview

import androidx.lifecycle.ViewModel
import com.spiderbiggen.manhwa.domain.model.Manhwa
import com.spiderbiggen.manhwa.domain.repository.ManhwaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ManhwaViewModel @Inject constructor(
    private val manhwaRepository: ManhwaRepository,
) : ViewModel() {

    private val mutableState = MutableStateFlow<ManhwaScreenState>(ManhwaScreenState.Loading)
    val state
        get() = mutableState.asStateFlow()

    suspend fun collect() {
        withContext(Dispatchers.IO) {
            manhwaRepository.getAll().collectLatest {
                mutableState.emit(ManhwaScreenState.Ready(mapResponse(it)))
            }
        }
    }

    private fun mapResponse(response: List<Manhwa>): List<Manhwa> {
        val map = response.groupBy {it.status}
        return buildList {
            addAll(map["Ongoing"].orEmpty())
            addAll(map["Completed"].orEmpty())
            addAll(map["Dropped"].orEmpty())
        }
    }
}