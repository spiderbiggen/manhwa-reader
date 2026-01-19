package com.spiderbiggen.manga.presentation.ui.profile.login

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import com.spiderbiggen.manga.domain.model.fold
import com.spiderbiggen.manga.domain.usecase.auth.Login
import com.spiderbiggen.manga.presentation.extensions.defaultScope
import com.spiderbiggen.manga.presentation.usecases.FormatAppError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(private val login: Login, private val formatAppError: FormatAppError) : ViewModel() {

    private val _state = MutableStateFlow<LoginState>(LoginState.Idle)
    val state = _state.asStateFlow()

    fun handleLogin(username: String, password: String) {
        defaultScope.launch {
            if (_state.value is LoginState.Loading) return@launch
            _state.emit(LoginState.Loading)
            val newState = login(username, password).fold(
                { LoginState.Success },
                { LoginState.Error(formatAppError(it)) },
            )
            _state.emit(newState)
        }
    }
}

@Immutable
sealed interface LoginState {
    data object Idle : LoginState
    data object Loading : LoginState
    data object Success : LoginState

    @Immutable
    data class Error(val message: String) : LoginState
}
