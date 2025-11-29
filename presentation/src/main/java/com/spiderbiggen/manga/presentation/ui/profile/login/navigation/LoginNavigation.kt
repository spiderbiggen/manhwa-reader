package com.spiderbiggen.manga.presentation.ui.profile.login.navigation

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.spiderbiggen.manga.presentation.ui.profile.login.LoginScreen
import kotlinx.serialization.Serializable

@Serializable
data object LoginRoute : NavKey

fun NavBackStack<NavKey>.navigateToLogin() {
    add(LoginRoute)
}

fun EntryProviderScope<NavKey>.loginDestination(
    onBackClick: () -> Unit,
    onRegisterClick: () -> Unit,
    onSuccess: () -> Unit,
) {
    entry<LoginRoute> {
        LoginScreen(
            viewModel = hiltViewModel(),
            onBackClick = onBackClick,
            onRegisterClick = onRegisterClick,
            onSuccess = onSuccess,
        )
    }
}
