package com.spiderbiggen.manga.presentation.ui.profile.overview

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.painterResource
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
import com.spiderbiggen.manga.presentation.ui.profile.overview.model.ProfileOverviewViewState
import com.spiderbiggen.manga.presentation.ui.profile.state.ProfileState
import kotlin.time.Clock.System.now

@Composable
fun ProfileOverview(
    viewModel: ProfileOverviewViewModel = hiltViewModel(),
    navigateBack: () -> Unit,
    navigateToLogin: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    ProfileOverviewContent(
        state = state,
        onBackClick = navigateBack,
        onLoginClick = navigateToLogin,
        onChangeAvatarClick = viewModel::handleChangeAvatar,
        onLogoutClick = viewModel::handleLogout,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileOverviewContent(
    state: ProfileOverviewViewState,
    onBackClick: () -> Unit = {},
    onLoginClick: () -> Unit = {},
    onChangeAvatarClick: (Uri) -> Unit = {},
    onLogoutClick: () -> Unit = {},
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
            is ProfileOverviewViewState.Authenticated -> AuthenticatedUserProfile(
                state = state,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(scaffoldPadding),
                onChangeAvatarClick = onChangeAvatarClick,
                onLogoutClick = onLogoutClick,
            )

            is ProfileOverviewViewState.Unauthenticated -> {
                UnauthenticatedUserProfile(scaffoldPadding, onLoginClick)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun AuthenticatedUserProfile(
    state: ProfileOverviewViewState.Authenticated,
    modifier: Modifier = Modifier,
    onChangeAvatarClick: (Uri) -> Unit = {},
    onLogoutClick: () -> Unit = {},
) {
    val pickMedia = rememberLauncherForActivityResult(PickVisualMedia()) { uri ->
        // Callback is invoked after the user selects a media item or closes the
        // photo picker.
        if (uri != null) {
            Log.d("PhotoPicker", "Selected URI: $uri")
            onChangeAvatarClick(uri)
        } else {
            Log.d("PhotoPicker", "No media selected")
        }
    }

    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Column(
            Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Box {
                key(state.avatarUrl, state.updatedAt) {
                    AsyncImage(
                        model = state.avatarUrl,
                        error = painterResource(R.drawable.account_circle),
                        contentDescription = "User Avatar",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(128.dp)
                            .clip(CircleShape),
                    )
                }
                FilledIconButton(
                    onClick = { pickMedia.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly)) },
                    modifier = Modifier.align(Alignment.BottomEnd),
                ) {
                    Icon(painterResource(R.drawable.edit), "Edit Avatar")
                }
            }
            Text(text = state.name, style = MaterialTheme.typography.headlineMediumEmphasized)
            Text(text = state.updatedAt.toString(), style = MaterialTheme.typography.bodySmall)
            state.email?.let {
                Text(text = it, style = MaterialTheme.typography.bodyLarge)
            }
            HorizontalDivider()
            Button(onClick = onLogoutClick) {
                Text("Logout")
            }
        }
    }
}

@Composable
private fun UnauthenticatedUserProfile(padding: PaddingValues, onLoginClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
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

@PreviewLightDark
@PreviewDynamicColors
@PreviewFontScale
@Composable
private fun ProfileOverviewPreview(
    @PreviewParameter(ProfileOverviewStatePreviewProvider::class) state: ProfileOverviewViewState,
) = MangaReaderTheme {
    ProfileOverviewContent(state)
}

private class ProfileOverviewStatePreviewProvider : PreviewParameterProvider<ProfileOverviewViewState> {
    override val values = sequenceOf(
        ProfileOverviewViewState.Unauthenticated,
        ProfileOverviewViewState.Authenticated(
            id = "",
            name = "Spiderbiggen",
            email = "spiderbiggen@gmail.com",
            avatarUrl = "example.com",
            updatedAt = now(),
        ),
    )
}
