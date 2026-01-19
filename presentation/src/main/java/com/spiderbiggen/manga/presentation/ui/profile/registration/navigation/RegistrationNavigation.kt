package com.spiderbiggen.manga.presentation.ui.profile.registration.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.spiderbiggen.manga.presentation.ui.profile.registration.RegistrationScreen
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@Serializable
data object RegistrationRoute : NavKey

fun NavBackStack<NavKey>.navigateToRegistration() {
    add(RegistrationRoute)
}

fun EntryProviderScope<NavKey>.registrationDestination(onBackClick: () -> Unit, onSuccess: () -> Unit) {
    entry<RegistrationRoute> {
        RegistrationScreen(
            viewModel = koinViewModel(),
            onBackClick = onBackClick,
            onSuccess = onSuccess,
        )
    }
}
