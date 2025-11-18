package com.spiderbiggen.manga.presentation.ui.profile.overview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spiderbiggen.manga.domain.usecase.user.GetUser
import com.spiderbiggen.manga.presentation.ui.profile.overview.model.ProfileViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    getUser: GetUser,
) : ViewModel() {

    val state: StateFlow<ProfileViewState> = getUser()
        .map { user ->
            if (user == null) {
                ProfileViewState.Unauthenticated
            } else {
                ProfileViewState.Authenticated(
                    name = user.username,
                    email = user.email,
                    avatarUrl = user.avatar,
                )
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ProfileViewState.Unauthenticated,
        )
}
