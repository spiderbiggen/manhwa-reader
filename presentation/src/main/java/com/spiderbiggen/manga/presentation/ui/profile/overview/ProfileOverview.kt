package com.spiderbiggen.manga.presentation.ui.profile.overview

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.PreviewDynamicColors
import androidx.compose.ui.tooling.preview.PreviewFontScale
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.spiderbiggen.manga.presentation.R
import com.spiderbiggen.manga.presentation.components.topappbar.MangaTopAppBar
import com.spiderbiggen.manga.presentation.components.topappbar.scrollWithContentBehavior
import com.spiderbiggen.manga.presentation.theme.MangaReaderTheme
import kotlin.time.Clock.System.now

@Composable
fun ProfileOverview(
    viewModel: ProfileOverviewViewModel,
    snackbarHostState: SnackbarHostState,
    onBackClick: () -> Unit,
    onLogout: () -> Unit,
) {
    LaunchedEffect(viewModel, snackbarHostState) {
        viewModel.snackbarFlow.collect {
            snackbarHostState.showSnackbar(it)
        }
    }

    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(state) {
        if (state is ProfileOverviewViewState.Unauthenticated) {
            onLogout()
        }
    }

    ProfileOverviewContent(
        state = state,
        snackbarHostState = snackbarHostState,
        onBackClick = onBackClick,
        onChangeAvatarClick = viewModel::handleChangeAvatar,
        onLogoutClick = viewModel::handleLogout,
        onSyncClick = viewModel::handleSync,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileOverviewContent(
    state: ProfileOverviewViewState,
    snackbarHostState: SnackbarHostState,
    onBackClick: () -> Unit = {},
    onChangeAvatarClick: (Uri) -> Unit = {},
    onLogoutClick: () -> Unit = {},
    onSyncClick: () -> Unit = {},
) {
    val topAppBarScrollBehavior = TopAppBarDefaults.scrollWithContentBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
        topBar = {
            MangaTopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(painterResource(R.drawable.arrow_back), "Back")
                    }
                },
                title = { Text("Profile") },
                scrollBehavior = topAppBarScrollBehavior,
            )
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        },
    ) { scaffoldPadding ->
        when (state) {
            is ProfileOverviewViewState.Authenticated -> AuthenticatedUserProfile(
                state = state,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(scaffoldPadding),
                onChangeAvatarClick = onChangeAvatarClick,
                onLogoutClick = onLogoutClick,
                onSyncClick = onSyncClick,
            )

            else -> {
                LoadingUserProfile(scaffoldPadding)
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
    onSyncClick: () -> Unit = {},
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
            state.email?.let {
                Text(text = it, style = MaterialTheme.typography.bodyLarge)
            }
            HorizontalDivider()

            Row(verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text(text = "Last Synchronization:")
                    Text(
                        text = state.lastSynchronizationTime ?: "Never",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                FilledIconButton(onSyncClick) {
                    Icon(painterResource(R.drawable.sync), contentDescription = "Sync")
                }
            }
            HorizontalDivider()

            Button(onClick = onLogoutClick) {
                Text("Logout")
            }
        }
    }
}

@Composable
private fun LoadingUserProfile(padding: PaddingValues) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("Not logged in")
    }
}

@PreviewLightDark
@PreviewDynamicColors
@PreviewFontScale
@Composable
private fun ProfileOverviewPreview(
    @PreviewParameter(ProfileOverviewStatePreviewProvider::class) state: ProfileOverviewViewState,
) = MangaReaderTheme {
    val snackbarHostState = remember { SnackbarHostState() }
    ProfileOverviewContent(
        state = state,
        snackbarHostState = snackbarHostState,
    )
}

private class ProfileOverviewStatePreviewProvider : PreviewParameterProvider<ProfileOverviewViewState> {
    override val values = sequenceOf(
        ProfileOverviewViewState.Authenticated(
            id = "",
            name = "Spiderbiggen",
            email = "spiderbiggen@gmail.com",
            avatarUrl = "example.com",
            updatedAt = now(),
            lastSynchronizationTime = "2025-12-15:23:16:23.000Z",
        ),
        ProfileOverviewViewState.Unknown,
        ProfileOverviewViewState.Unauthenticated,
    )
}
