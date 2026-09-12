package com.spiderbiggen.manga.presentation.ui.profile.registration

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentType
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.PreviewDynamicColors
import androidx.compose.ui.tooling.preview.PreviewFontScale
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.spiderbiggen.manga.presentation.R
import com.spiderbiggen.manga.presentation.components.input.ButtonWithLoadingState
import com.spiderbiggen.manga.presentation.components.input.PasswordTextField
import com.spiderbiggen.manga.presentation.extensions.asString
import com.spiderbiggen.manga.presentation.framework.adapter.InterruptBackHandler
import com.spiderbiggen.manga.presentation.theme.MangaReaderTheme
import com.spiderbiggen.manga.presentation.ui.main.LocalAppVersion

@Composable
fun RegistrationScreen(
    viewModel: RegistrationViewModel,
    onBackClick: () -> Unit,
    onSuccess: () -> Unit,
) {
    val registrationState by viewModel.state.collectAsStateWithLifecycle()

    val onSuccessState = rememberUpdatedState(onSuccess)
    SideEffect(registrationState) {
        if (registrationState is RegistrationState.Success) {
            onSuccessState.value()
        }
    }

    RegistrationScreenContent(
        registrationState = registrationState,
        onBackClick = {
            if (registrationState !is RegistrationState.Loading) {
                onBackClick()
            }
        },
        onRegister = viewModel::handleRegister,
    )
}

@Composable
private fun RegistrationScreenContent(
    registrationState: RegistrationState,
    onBackClick: () -> Unit = {},
    onRegister: (String, String, String) -> Unit = { _, _, _ -> },
) {
    val isLoading = registrationState is RegistrationState.Loading
    InterruptBackHandler(enabled = isLoading)

    val username = rememberTextFieldState()
    val email = rememberTextFieldState()
    val password = rememberTextFieldState()

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBackClick, enabled = !isLoading) {
                        Icon(painterResource(R.drawable.arrow_back), "Back")
                    }
                },
                title = { Text("Register") },
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            UsernameTextField(
                state = username,
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth(),
            )
            EmailTextField(
                state = email,
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth(),
            )
            PasswordTextField(
                state = password,
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth(),
                onKeyboardAction = { performDefaultAction ->
                    onRegister(username.asString(), email.asString(), password.asString())
                    performDefaultAction()
                },
            )

            RegistrationStateContent(
                registrationState = registrationState,
                onRegisterClick = {
                    onRegister(username.asString(), email.asString(), password.asString())
                },
            )

            Spacer(Modifier.weight(1f))
            Text(LocalAppVersion.current, style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
private fun UsernameTextField(
    state: TextFieldState,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    OutlinedTextField(
        state = state,
        label = { Text("Username") },
        enabled = enabled,
        modifier =
            modifier.semantics {
                contentType = ContentType.NewUsername
            },
        lineLimits = TextFieldLineLimits.SingleLine,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
    )
}

@Composable
private fun EmailTextField(
    state: TextFieldState,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    OutlinedTextField(
        state = state,
        label = { Text("Email") },
        enabled = enabled,
        modifier =
            modifier.semantics {
                contentType = ContentType.EmailAddress
            },
        lineLimits = TextFieldLineLimits.SingleLine,
        keyboardOptions =
            KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next,
            ),
    )
}

@Composable
private fun ColumnScope.RegistrationStateContent(
    registrationState: RegistrationState,
    onRegisterClick: () -> Unit,
) {
    if (registrationState is RegistrationState.Error) {
        Text(
            text = registrationState.message,
            color = MaterialTheme.colorScheme.error,
        )
    }
    ButtonWithLoadingState(
        isLoading = registrationState is RegistrationState.Loading,
        onClick = onRegisterClick,
        content = { Text("Register") },
    )
}

@PreviewLightDark
@PreviewDynamicColors
@PreviewFontScale
@Composable
private fun RegistrationScreenPreview(
    @PreviewParameter(RegistrationStatePreviewProvider::class) data: RegistrationState
) = MangaReaderTheme {
    CompositionLocalProvider(LocalAppVersion provides "1.23.0 (66)") {
        RegistrationScreenContent(data)
    }
}

private class RegistrationStatePreviewProvider : PreviewParameterProvider<RegistrationState> {
    override val values =
        sequenceOf(
            RegistrationState.Idle,
            RegistrationState.Loading,
            RegistrationState.Error("Could not register"),
        )
}
