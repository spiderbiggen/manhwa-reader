package com.spiderbiggen.manga.presentation.ui.profile.registration

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedSecureTextField
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.PreviewDynamicColors
import androidx.compose.ui.tooling.preview.PreviewFontScale
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.spiderbiggen.manga.presentation.R
import com.spiderbiggen.manga.presentation.components.topappbar.rememberTopAppBarState
import com.spiderbiggen.manga.presentation.theme.MangaReaderTheme

@Composable
fun RegistrationScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    navigateBack: () -> Unit,
) {
    val loginData by viewModel.loginState.collectAsState()
    val state = rememberUpdatedState(newValue = loginData.state)

    LaunchedEffect(state.value) {
        if (state.value is LoginState.Success) {
            navigateBack()
        }
    }

    LoginScreenContent(
        loginData = loginData,
        onBackClick = {
            if (state.value !is LoginState.Loading) {
                navigateBack()
            }
        },
        onToggleType = viewModel::toggleType,
        onLogin = viewModel::handleLogin,
        onRegister = viewModel::handleRegister,
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun LoginScreenContent(
    loginData: LoginData,
    onBackClick: () -> Unit = {},
    onToggleType: () -> Unit = {},
    onLogin: (String, String) -> Unit = { _, _ -> },
    onRegister: (String, String, String) -> Unit = { _, _, _ -> },
) {

    val topAppBarState = rememberTopAppBarState()

    val loginState = loginData.state

    val username = rememberTextFieldState()
    val email = rememberTextFieldState()
    val password = rememberTextFieldState()

    Scaffold(
        contentWindowInsets = WindowInsets.systemBars,
        topBar = {
            Box(
                Modifier
                    .onSizeChanged { topAppBarState.appBarHeight = it.height.toFloat() }
                    .background(MaterialTheme.colorScheme.background)
                    .windowInsetsPadding(TopAppBarDefaults.windowInsets),
            ) {
                TopAppBar(
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(painterResource(R.drawable.arrow_back), "Back")
                        }
                    },
                    title = {
                        when (loginData.type) {
                            LoginType.Login -> Text("Login")
                            LoginType.Register -> Text("Register")
                        }
                    },
                )
            }
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
                modifier = Modifier.fillMaxWidth(),
                lineLimits = TextFieldLineLimits.SingleLine,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next,
                ),
            )
            if (loginData.type == LoginType.Register) {
                OutlinedTextField(
                    state = email,
                    label = { Text("Email") },
                    enabled = loginState !is LoginState.Loading,
                    modifier = Modifier.fillMaxWidth(),
                    lineLimits = TextFieldLineLimits.SingleLine,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next,
                    ),
                )
            }
            OutlinedSecureTextField(
                state = password,
                label = { Text("Password") },
                enabled = loginState !is LoginState.Loading,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Go,
                ),
                onKeyboardAction = {
                    // TODO clean up
                    if (loginState !is LoginState.Loading) {
                        when (loginData.type) {
                            LoginType.Login -> onLogin(
                                username.text.toString(),
                                password.text.toString(),
                            )

                            LoginType.Register -> onRegister(
                                username.text.toString(),
                                email.text.toString(),
                                password.text.toString(),
                            )
                        }
                    }
                },
            )
            if (loginState is LoginState.Error) {
                Text(
                    text = loginState.message,
                    color = MaterialTheme.colorScheme.error,
                )
            }
            Row(
                Modifier.width(IntrinsicSize.Min),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                if (loginState is LoginState.Loading) {
                    LoadingIndicator()
                } else if (loginData.type == LoginType.Login) {
                    Button(
                        onClick = {
                            onLogin(
                                username.text.toString(),
                                password.text.toString(),
                            )
                        },
                        modifier = Modifier
                            .width(IntrinsicSize.Max)
                            .weight(1f),
                    ) {
                        Text("Login")
                    }
                    Button(
                        onClick = onToggleType,
                        modifier = Modifier
                            .width(IntrinsicSize.Max)
                            .weight(1f),
                    ) {
                        Text("To Registration")
                    }
                } else {
                    Button(
                        onClick = {
                            onRegister(
                                username.text.toString(),
                                email.text.toString(),
                                password.text.toString(),
                            )
                        },
                        modifier = Modifier
                            .width(IntrinsicSize.Max)
                            .weight(1f),
                    ) {
                        Text("Register")
                    }
                    Button(
                        onClick = onToggleType,
                        modifier = Modifier
                            .width(IntrinsicSize.Max)
                            .weight(1f),
                    ) {
                        Text("Switch to login")
                    }
                }
            }
        }
    }
}

@PreviewLightDark
@PreviewDynamicColors
@PreviewFontScale
@Composable
private fun LoginScreenPreview(
    @PreviewParameter(LoginDataPreviewProvider::class) data: LoginData,
) = MangaReaderTheme {
    LoginScreenContent(loginData = data)
}

private class LoginDataPreviewProvider : PreviewParameterProvider<LoginData> {
    override val values = sequenceOf(
        LoginData(type = LoginType.Login, state = LoginState.Idle),
        LoginData(type = LoginType.Login, state = LoginState.Loading),
        LoginData(type = LoginType.Login, state = LoginState.Error("Could not log in")),
        LoginData(type = LoginType.Register, state = LoginState.Idle),
        LoginData(type = LoginType.Register, state = LoginState.Loading),
        LoginData(type = LoginType.Register, state = LoginState.Error("Could not register")),
    )
}
