package com.spiderbiggen.manga.presentation.ui.profile.registration

import androidx.compose.runtime.Immutable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.spiderbiggen.manga.domain.model.AppError
import com.spiderbiggen.manga.domain.model.Either
import com.spiderbiggen.manga.domain.usecase.auth.Login
import com.spiderbiggen.manga.domain.usecase.auth.Register
import com.spiderbiggen.manga.presentation.extensions.defaultScope
import com.spiderbiggen.manga.presentation.usecases.FormatAppError
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val login: Login,
    private val register: Register,
    private val formatAppError: FormatAppError,
) : ViewModel() {

    private var type: LoginType
        get() = savedStateHandle.get<LoginType>(TYPE_KEY) ?: LoginType.Login
        set(value) = savedStateHandle.set(TYPE_KEY, value)
    private val _loginState = MutableStateFlow(LoginData(type = type))
    val loginState = _loginState.asStateFlow()

    fun toggleType() {
        type = when (type) {
            LoginType.Login -> LoginType.Register
            else -> LoginType.Login
        }
        _loginState.update { it.copy(type = type) }
    }

    fun handleLogin(username: String, password: String) {
        defaultScope.launch {
            if (_loginState.value.state is LoginState.Loading) return@launch
            _loginState.update { it.copy(state = LoginState.Loading) }
            val newState = when (val result = login(username, password)) {
                is Either.Left<*, *> -> LoginState.Success
                is Either.Right<*, AppError> -> LoginState.Error(formatAppError(result.value))
            }
            _loginState.update { it.copy(state = newState) }
        }
    }

    fun handleRegister(username: String, email: String, password: String) {
        defaultScope.launch {
            if (_loginState.value.state is LoginState.Loading) return@launch
            _loginState.update { it.copy(state = LoginState.Loading) }
            val newState = when (val result = register(username, email, password)) {
                is Either.Left<*, *> -> LoginState.Success
                is Either.Right<*, AppError> -> LoginState.Error(formatAppError(result.value))
            }
            _loginState.update { it.copy(state = newState) }
        }
    }

    private companion object {
        private const val TYPE_KEY = "key_login_type"
    }
}

enum class LoginType {
    Login, Register
}

@Immutable
data class LoginData(
    val type: LoginType = LoginType.Login,
    val state: LoginState = LoginState.Idle,
)

@Immutable
sealed interface LoginState {
    data object Idle : LoginState
    data object Loading : LoginState
    data object Success : LoginState

    @Immutable
    data class Error(val message: String) : LoginState
}
