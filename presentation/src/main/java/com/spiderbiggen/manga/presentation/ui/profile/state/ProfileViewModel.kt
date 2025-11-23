package com.spiderbiggen.manga.presentation.ui.profile.state

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spiderbiggen.manga.domain.usecase.user.GetUser
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class ProfileViewModel @Inject constructor(getUser: GetUser) : ViewModel() {

    val state: StateFlow<ProfileState> = getUser()
        .map { user ->
            if (user == null) {
                ProfileState.Unauthenticated
            } else {
                ProfileState.Authenticated(
                    name = user.username,
                    avatarUrl = user.avatarUrl,
                )
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ProfileState.Unauthenticated,
        )
}
