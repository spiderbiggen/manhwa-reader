package com.spiderbiggen.manga.presentation.ui.profile.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.KeyboardActionHandler
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedSecureTextField
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentType
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.withLink
import androidx.compose.ui.tooling.preview.PreviewDynamicColors
import androidx.compose.ui.tooling.preview.PreviewFontScale
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.spiderbiggen.manga.presentation.R
import com.spiderbiggen.manga.presentation.framework.adapter.InterruptBackHandler
import com.spiderbiggen.manga.presentation.theme.MangaReaderTheme
import com.spiderbiggen.manga.presentation.ui.main.LocalAppVersion
import kotlin.toString
import org.w3c.dom.Text

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onBackClick: () -> Unit,
    onRegisterClick: () -> Unit,
    onSuccess: () -> Unit,
) {
    val loginState by viewModel.state.collectAsStateWithLifecycle()

    val onSuccessState = rememberUpdatedState(onSuccess)
    LaunchedEffect(loginState) {
        if (loginState is LoginState.Success) {
            onSuccessState.value()
        }
    }

    LoginScreenContent(
        loginState = loginState,
        onBackClick = {
            if (loginState !is LoginState.Loading) {
                onBackClick()
            }
        },
        onLogin = viewModel::handleLogin,
        onRegisterClick = onRegisterClick,
    )
}

@Composable
private fun LoginScreenContent(
    loginState: LoginState,
    onBackClick: () -> Unit = {},
    onLogin: (String, String) -> Unit = { _, _ -> },
    onRegisterClick: () -> Unit = {},
) {
    val isLoading = loginState is LoginState.Loading
    InterruptBackHandler(enabled = isLoading)

    val username = rememberTextFieldState()
    val password = rememberTextFieldState()

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBackClick, enabled = !isLoading) {
                        Icon(painterResource(R.drawable.arrow_back), "Back")
                    }
                },
                title = { Text("Login") },
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
            PasswordTextField(
                state = password,
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth(),
                onKeyboardAction = { performDefaultAction ->
                    onLogin(username.text.toString(), password.text.toString())
                    performDefaultAction()
                },
            )
            LoginStateContent(
                loginState = loginState,
                onLoginClick = {
                    onLogin(username.text.toString(), password.text.toString())
                },
                onRegisterClick = onRegisterClick,
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
                contentType = ContentType.Username + ContentType.EmailAddress
            },
        lineLimits = TextFieldLineLimits.SingleLine,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
    )
}

@Composable
private fun PasswordTextField(
    state: TextFieldState,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onKeyboardAction: KeyboardActionHandler? = null,
) {
    OutlinedSecureTextField(
        state = state,
        label = { Text("Password") },
        enabled = enabled,
        modifier =
            modifier.semantics {
                contentType = ContentType.Password
            },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Go),
        onKeyboardAction = onKeyboardAction,
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun ColumnScope.LoginStateContent(
    loginState: LoginState,
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit,
) {
    if (loginState is LoginState.Error) {
        Text(
            text = loginState.message,
            color = MaterialTheme.colorScheme.error,
        )
    }

    when (loginState) {
        is LoginState.Loading -> LoadingIndicator()

        else -> {
            Button(onClick = onLoginClick) {
                Text("Login")
            }
            HorizontalDivider()
            Text(
                text =
                    buildAnnotatedString {
                        append("Don't have an account? ")
                        withLink(
                            LinkAnnotation.Clickable("Register") {
                                onRegisterClick()
                            }
                        ) {
                            append("Register instead")
                        }
                    }
            )
        }
    }
}

@PreviewLightDark
@PreviewDynamicColors
@PreviewFontScale
@Composable
private fun LoginScreenPreview(
    @PreviewParameter(LoginStatePreviewProvider::class) state: LoginState
) = MangaReaderTheme {
    CompositionLocalProvider(LocalAppVersion provides "1.23.0 (66)") {
        LoginScreenContent(loginState = state)
    }
}

private class LoginStatePreviewProvider : PreviewParameterProvider<LoginState> {
    override val values =
        sequenceOf(
            LoginState.Idle,
            LoginState.Loading,
            LoginState.Error("Could not log in"),
        )
}
