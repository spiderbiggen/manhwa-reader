package com.spiderbiggen.manga.presentation.ui.profile.registration

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import com.spiderbiggen.manga.presentation.components.InterruptBackHandler
import com.spiderbiggen.manga.presentation.theme.MangaReaderTheme
import com.spiderbiggen.manga.presentation.ui.main.LocalAppVersion
import org.koin.androidx.compose.koinViewModel

@Composable
fun RegistrationScreen(
    viewModel: RegistrationViewModel = koinViewModel(),
    onBackClick: () -> Unit,
    onSuccess: () -> Unit,
) {
    val registrationState by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(registrationState) {
        if (registrationState is RegistrationState.Success) {
            onSuccess()
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
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
                title = {
                    Text("Register")
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
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics {
                        contentType = ContentType.NewUsername
                    },
                lineLimits = TextFieldLineLimits.SingleLine,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next,
                ),
            )
            OutlinedTextField(
                state = email,
                label = { Text("Email") },
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics {
                        contentType = ContentType.EmailAddress
                    },
                lineLimits = TextFieldLineLimits.SingleLine,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next,
                ),
            )
            OutlinedSecureTextField(
                state = password,
                label = { Text("Password") },
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics {
                        contentType = ContentType.NewPassword
                    },
                keyboardOptions = KeyboardOptions(
                    autoCorrectEnabled = false,
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Go,
                ),
                onKeyboardAction = {
                    onRegister(
                        username.text.toString(),
                        email.text.toString(),
                        password.text.toString(),
                    )
                },
            )
            if (registrationState is RegistrationState.Error) {
                Text(
                    text = registrationState.message,
                    color = MaterialTheme.colorScheme.error,
                )
            }
            if (isLoading) {
                LoadingIndicator()
            } else {
                Button(
                    onClick = {
                        onRegister(
                            username.text.toString(),
                            email.text.toString(),
                            password.text.toString(),
                        )
                    },
                ) {
                    Text("Register")
                }
            }
            Spacer(Modifier.weight(1f))
            Text(LocalAppVersion.current, style = MaterialTheme.typography.labelSmall)
        }
    }
}

@PreviewLightDark
@PreviewDynamicColors
@PreviewFontScale
@Composable
private fun RegistrationScreenPreview(
    @PreviewParameter(RegistrationStatePreviewProvider::class) data: RegistrationState,
) = MangaReaderTheme {
    CompositionLocalProvider(LocalAppVersion provides "1.23.0 (66)") {
        RegistrationScreenContent(data)
    }
}

private class RegistrationStatePreviewProvider : PreviewParameterProvider<RegistrationState> {
    override val values = sequenceOf(
        RegistrationState.Idle,
        RegistrationState.Loading,
        RegistrationState.Error("Could not register"),
    )
}
