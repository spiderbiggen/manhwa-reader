package com.spiderbiggen.manga.presentation.ui.profile.state

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spiderbiggen.manga.domain.model.leftOrElse
import com.spiderbiggen.manga.domain.usecase.user.GetUser
import com.spiderbiggen.manga.domain.usecase.user.SynchronizeWithRemote
import com.spiderbiggen.manga.presentation.components.snackbar.SnackbarData
import com.spiderbiggen.manga.presentation.extensions.launchDefault
import com.spiderbiggen.manga.presentation.usecases.FormatAppError
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.yield

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val formatAppError: FormatAppError,
    private val getUser: GetUser,
    private val synchronizeWithRemote: SynchronizeWithRemote,
) : ViewModel() {

    private val _snackbarFlow = MutableSharedFlow<SnackbarData>(1)
    val snackbarFlow: SharedFlow<SnackbarData>
        get() = _snackbarFlow.asSharedFlow()

    private val isRefreshing: MutableStateFlow<Boolean> = MutableStateFlow(false)

    val state: StateFlow<ProfileState> = screenStateFlow()
        .onStart { onStart() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ProfileState.Unauthenticated,
        )

    private suspend fun onStart() = launchDefault {
        synchronize()
    }

    private fun screenStateFlow() = combine(
        getUser(),
        isRefreshing,
    ) { user, isRefreshing ->
        if (user == null) {
            ProfileState.Unauthenticated
        } else {
            ProfileState.Authenticated(
                name = user.username,
                avatarUrl = user.avatarUrl,
                refreshing = isRefreshing,
            )
        }
    }

    private suspend fun synchronize() {
        isRefreshing.emit(true)
        synchronizeWithRemote(ignoreInterval = false).leftOrElse {
            _snackbarFlow.emit(SnackbarData(formatAppError(it)))
        }
        yield()
        isRefreshing.emit(false)
    }
}
