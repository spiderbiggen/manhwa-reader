package com.spiderbiggen.manga.presentation.ui.profile.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.spiderbiggen.manga.presentation.R
import com.spiderbiggen.manga.presentation.components.InterruptBackHandler
import com.spiderbiggen.manga.presentation.theme.MangaReaderTheme

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    navigateBack: () -> Unit,
    navigateToRegistration: () -> Unit,
) {
    val loginState by viewModel.state.collectAsState()

    LaunchedEffect(loginState) {
        if (loginState is LoginState.Success) {
            navigateBack()
        }
    }

    LoginScreenContent(
        loginState = loginState,
        onBackClick = {
            if (loginState !is LoginState.Loading) {
                navigateBack()
            }
        },
        onLogin = viewModel::handleLogin,
        onRegistrationClick = navigateToRegistration,
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun LoginScreenContent(
    loginState: LoginState,
    onBackClick: () -> Unit = {},
    onLogin: (String, String) -> Unit = { _, _ -> },
    onRegistrationClick: () -> Unit = {},
) {
    InterruptBackHandler(enabled = loginState is LoginState.Loading)

    val username = rememberTextFieldState()
    val password = rememberTextFieldState()

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBackClick, enabled = loginState !is LoginState.Loading) {
                        Icon(painterResource(R.drawable.arrow_back), "Back")
                    }
                },
                title = {
                    Text("Login")
                },
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            OutlinedTextField(
                state = username,
                label = { Text("Username") },
                enabled = loginState !is LoginState.Loading,
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics {
                        contentType = ContentType.Username + ContentType.EmailAddress
                    },
                lineLimits = TextFieldLineLimits.SingleLine,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next,
                ),
            )
            OutlinedSecureTextField(
                state = password,
                label = { Text("Password") },
                enabled = loginState !is LoginState.Loading,
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics {
                        contentType = ContentType.Password
                    },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Go,
                ),
                onKeyboardAction = {
                    onLogin(
                        username.text.toString(),
                        password.text.toString(),
                    )
                },
            )
            if (loginState is LoginState.Error) {
                Text(
                    text = loginState.message,
                    color = MaterialTheme.colorScheme.error,
                )
            }
            when (loginState) {
                is LoginState.Loading -> LoadingIndicator()

                else -> {
                    Button(
                        onClick = {
                            onLogin(
                                username.text.toString(),
                                password.text.toString(),
                            )
                        },
                    ) {
                        Text("Login")
                    }
                    HorizontalDivider()
                    Text(
                        text = buildAnnotatedString {
                            this.append("Don't have an account? ")
                            this.withLink(
                                LinkAnnotation.Clickable("Register") {
                                    onRegistrationClick()
                                },
                            ) {
                                append("Register instead")
                            }
                        },
                    )
                }
            }
        }
    }
}

@PreviewLightDark
@PreviewDynamicColors
@PreviewFontScale
@Composable
private fun LoginScreenPreview(@PreviewParameter(LoginStatePreviewProvider::class) state: LoginState) =
    MangaReaderTheme {
        LoginScreenContent(loginState = state)
    }

private class LoginStatePreviewProvider : PreviewParameterProvider<LoginState> {
    override val values = sequenceOf(
        LoginState.Idle,
        LoginState.Loading,
        LoginState.Error("Could not log in"),
    )
}
