package com.spiderbiggen.manga.presentation.components.input

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.KeyboardActionHandler
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.OutlinedSecureTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.semantics.contentType
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType

@Composable
fun PasswordTextField(
    state: TextFieldState,
    modifier: Modifier = Modifier,
    imeAction: ImeAction = ImeAction.Go,
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
        keyboardOptions =
            KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = imeAction,
            ),
        onKeyboardAction = onKeyboardAction,
    )
}
