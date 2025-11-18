package com.spiderbiggen.manga.presentation.ui.profile.overview.model

import androidx.compose.runtime.Immutable

@Immutable
sealed interface ProfileViewState {
    @Immutable
    data object Unauthenticated : ProfileViewState

    @Immutable
    data class Authenticated(
        val avatarUrl: String?,
        val name: String,
        val email: String?,
    ): ProfileViewState
}
