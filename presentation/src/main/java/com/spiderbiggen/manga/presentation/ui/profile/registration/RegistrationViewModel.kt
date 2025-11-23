package com.spiderbiggen.manga.presentation.ui.profile.registration

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import com.spiderbiggen.manga.domain.model.AppError
import com.spiderbiggen.manga.domain.model.Either
import com.spiderbiggen.manga.domain.usecase.auth.Register
import com.spiderbiggen.manga.presentation.extensions.defaultScope
import com.spiderbiggen.manga.presentation.usecases.FormatAppError
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class LoginViewModel @Inject constructor(private val register: Register, private val formatAppError: FormatAppError) :
    ViewModel() {
    private val _state = MutableStateFlow<RegistrationState>(RegistrationState.Idle)
    val state = _state.asStateFlow()

    fun handleRegister(username: String, email: String, password: String) {
        defaultScope.launch {
            if (_state.value is RegistrationState.Loading) return@launch
            _state.emit(RegistrationState.Loading)
            val newState = when (val result = register(username, email, password)) {
                is Either.Left<*, *> -> RegistrationState.Success
                is Either.Right<*, AppError> -> RegistrationState.Error(formatAppError(result.value))
            }
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
