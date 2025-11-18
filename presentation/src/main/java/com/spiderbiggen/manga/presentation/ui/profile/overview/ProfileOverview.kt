package com.spiderbiggen.manga.presentation.ui.profile.overview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewDynamicColors
import androidx.compose.ui.tooling.preview.PreviewFontScale
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.spiderbiggen.manga.presentation.R
import com.spiderbiggen.manga.presentation.components.MangaScaffold
import com.spiderbiggen.manga.presentation.components.topappbar.rememberTopAppBarState
import com.spiderbiggen.manga.presentation.theme.MangaReaderTheme
import com.spiderbiggen.manga.presentation.ui.profile.overview.model.ProfileViewState

@Composable
fun ProfileOverview(
    viewModel: ProfileViewModel = hiltViewModel(),
    navigateBack: () -> Unit,
    navigateToLogin: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    ProfileOverviewContent(
        state = state,
        onBackClick = navigateBack,
        onLoginClick = navigateToLogin,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileOverviewContent(
    state: ProfileViewState,
    onBackClick: () -> Unit,
    onLoginClick: () -> Unit,
) {
    val topAppBarState = rememberTopAppBarState()

    MangaScaffold(
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
                    title = { Text("Profile") },
                )
            }
        },
        topBarOffset = { topAppBarState.appBarOffset.floatValue.toInt() },
    ) { scaffoldPadding ->
        when (state) {
            is ProfileViewState.Authenticated -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(scaffoldPadding)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    state.avatarUrl?.let {
                        AsyncImage(
                            model = it,
                            contentDescription = "User Avatar",
                            modifier = Modifier
                                .size(128.dp)
                                .clip(CircleShape),
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = state.name, style = MaterialTheme.typography.headlineSmall)
                    state.email?.let {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = it, style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }

            is ProfileViewState.Unauthenticated -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(scaffoldPadding)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text("Not logged in")
                    Button(onClick = onLoginClick) {
                        Text("Login")
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
private fun ProfileOverviewPreview(
    @PreviewParameter(ProfileViewStatePreviewProvider::class) state: ProfileViewState,
) = MangaReaderTheme {
    ProfileOverviewContent(
        state = state,
        onBackClick = {},
        onLoginClick = {},
    )
}

private class ProfileViewStatePreviewProvider : PreviewParameterProvider<ProfileViewState> {
    override val values = sequenceOf(
        ProfileViewState.Unauthenticated,
        ProfileViewState.Authenticated(
            name = "Spiderbiggen",
            email = "spiderbiggen@gmail.com",
            avatarUrl = null,
        ),
    )
}
