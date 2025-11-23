package com.spiderbiggen.manga.presentation.ui.profile.state

import androidx.compose.runtime.Immutable
import kotlin.time.Instant

@Immutable
sealed interface ProfileState {
    @Immutable
    data object Unauthenticated : ProfileState

    @Immutable
    data class Authenticated(val name: String, val avatarUrl: String) : ProfileState
}
