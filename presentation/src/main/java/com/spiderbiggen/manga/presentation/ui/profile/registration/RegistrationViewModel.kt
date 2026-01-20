package com.spiderbiggen.manga.presentation.ui.profile.registration

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import com.spiderbiggen.manga.domain.usecase.auth.Register
import com.spiderbiggen.manga.presentation.extensions.defaultScope
import com.spiderbiggen.manga.presentation.usecases.FormatAppError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RegistrationViewModel(private val register: Register, private val formatAppError: FormatAppError) : ViewModel() {
    private val _state = MutableStateFlow<RegistrationState>(RegistrationState.Idle)
    val state = _state.asStateFlow()

    fun handleRegister(username: String, email: String, password: String) {
        defaultScope.launch {
            if (_state.value is RegistrationState.Loading) return@launch
            _state.emit(RegistrationState.Loading)
            val newState = register(username, email, password).fold(
                { RegistrationState.Error(formatAppError(it)) },
                { RegistrationState.Success },
            )
            _state.emit(newState)
        }
    }
}

@Immutable
sealed interface RegistrationState {
    data object Idle : RegistrationState
    data object Loading : RegistrationState
    data object Success : RegistrationState

    @Immutable
    data class Error(val message: String) : RegistrationState
}
