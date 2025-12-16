package com.spiderbiggen.manga.presentation.ui.profile.overview

import androidx.compose.runtime.Immutable
import kotlin.time.Instant

@Immutable
sealed interface ProfileOverviewViewState {
    @Immutable
    data object Unknown : ProfileOverviewViewState

    @Immutable
    data object Unauthenticated : ProfileOverviewViewState

    @Immutable
    data class Authenticated(
        val id: String,
        val name: String,
        val avatarUrl: String,
        val email: String?,
        val updatedAt: Instant,
        val lastSynchronizationTime: String?,
    ) : ProfileOverviewViewState
}
