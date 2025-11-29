package com.spiderbiggen.manga.presentation.ui.profile.navigation

import androidx.compose.material3.SnackbarHostState
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.spiderbiggen.manga.presentation.navigation.popUpToInclusive
import com.spiderbiggen.manga.presentation.ui.profile.login.navigation.LoginRoute
import com.spiderbiggen.manga.presentation.ui.profile.login.navigation.loginDestination
import com.spiderbiggen.manga.presentation.ui.profile.login.navigation.navigateToLogin
import com.spiderbiggen.manga.presentation.ui.profile.overview.navigation.ProfileOverviewRoute
import com.spiderbiggen.manga.presentation.ui.profile.overview.navigation.navigateToProfileOverview
import com.spiderbiggen.manga.presentation.ui.profile.overview.navigation.profileOverviewDestination
import com.spiderbiggen.manga.presentation.ui.profile.registration.navigation.navigateToRegistration
import com.spiderbiggen.manga.presentation.ui.profile.registration.navigation.registrationDestination
import com.spiderbiggen.manga.presentation.ui.profile.state.ProfileState

fun NavBackStack<NavKey>.navigateToProfile(state: ProfileState) {
    if (state is ProfileState.Authenticated) navigateToProfileOverview() else navigateToLogin()
}

fun EntryProviderScope<NavKey>.profile(backStack: NavBackStack<NavKey>, snackbarHostState: SnackbarHostState) {
    profileOverviewDestination(
        snackbarHostState = snackbarHostState,
        onBackClick = { backStack.popUpToInclusive<ProfileOverviewRoute>() },
        onLogout = { backStack[backStack.lastIndex] = LoginRoute },
    )
    loginDestination(
        onBackClick = { backStack.removeLastOrNull() },
        onRegisterClick = backStack::navigateToRegistration,
        onSuccess = { backStack[backStack.lastIndex] = ProfileOverviewRoute },
    )
    registrationDestination(
        onBackClick = { backStack.removeLastOrNull() },
        onSuccess = {
            backStack.removeAll { it is LoginRoute }
            backStack[backStack.lastIndex] = ProfileOverviewRoute
        },
    )
}
