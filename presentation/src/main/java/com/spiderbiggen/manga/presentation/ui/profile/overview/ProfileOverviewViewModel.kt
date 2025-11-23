package com.spiderbiggen.manga.presentation.ui.profile.overview

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spiderbiggen.manga.domain.model.mapRight
import com.spiderbiggen.manga.domain.usecase.auth.Logout
import com.spiderbiggen.manga.domain.usecase.user.GetUser
import com.spiderbiggen.manga.domain.usecase.user.profile.UpdateAvatar
import com.spiderbiggen.manga.presentation.extensions.defaultScope
import com.spiderbiggen.manga.presentation.ui.profile.overview.model.ProfileOverviewViewState
import com.spiderbiggen.manga.presentation.usecases.FormatAppError
import dagger.hilt.android.lifecycle.HiltViewModel
import java.net.URI
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class ProfileOverviewViewModel @Inject constructor(
    getUser: GetUser,
    private val logout: Logout,
    private val updateAvatar: UpdateAvatar,
    private val formatAppError: FormatAppError,
) : ViewModel() {

    val state: StateFlow<ProfileOverviewViewState> = getUser()
        .map { user ->
            if (user == null) {
                ProfileOverviewViewState.Unauthenticated
            } else {
                ProfileOverviewViewState.Authenticated(
                    id = user.id,
                    name = user.username,
                    avatarUrl = user.avatarUrl,
                    email = user.email,
                    updatedAt = user.updatedAt,
                )
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ProfileOverviewViewState.Unauthenticated,
        )

    fun handleLogout() {
        defaultScope.launch {
            logout().mapRight { println(it) }
        }
    }

    fun handleChangeAvatar(uri: Uri) {
        defaultScope.launch {
            updateAvatar(URI.create(uri.toString())).mapRight {
                Log.e("ProfileOverviewViewModel", formatAppError(it))
            }
        }
    }
}
