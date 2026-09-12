package com.spiderbiggen.manga.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.WavyProgressIndicatorDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.spiderbiggen.manga.presentation.R
import com.spiderbiggen.manga.presentation.components.animation.ExpressiveAnimatedVisibility
import com.spiderbiggen.manga.presentation.ui.profile.state.ProfileState

@Composable
fun ProfileIcon(profileState: ProfileState, modifier: Modifier = Modifier) {
    when (profileState) {
        is ProfileState.Unauthenticated -> ProfileIconUnauthenticated(modifier)
        is ProfileState.Authenticated ->
            ProfileIconAuthenticated(
                avatar = profileState.avatarUrl,
                refreshing = profileState.refreshing,
                modifier = modifier,
            )
    }
}

@Composable
fun ProfileIconUnauthenticated(modifier: Modifier = Modifier) {
    Icon(
        painterResource(R.drawable.account_circle),
        contentDescription = "Profile",
        modifier = modifier,
    )
}

@Composable
fun ProfileIconAuthenticated(
    avatar: String,
    modifier: Modifier = Modifier,
    refreshing: Boolean = false,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier =
            modifier
                .defaultMinSize(
                    WavyProgressIndicatorDefaults.CircularContainerSize,
                    WavyProgressIndicatorDefaults.CircularContainerSize,
                )
                .aspectRatio(1f),
    ) {
        AsyncImage(
            model = avatar,
            contentDescription = "Profile",
            contentScale = ContentScale.Crop,
            error = painterResource(R.drawable.account_circle),
            modifier = Modifier.matchParentSize().padding(all = 8.dp).clip(CircleShape),
        )
        ExpressiveAnimatedVisibility(
            visible = refreshing,
            modifier = Modifier.matchParentSize(),
        ) {
            CircularWavyProgressIndicator(Modifier.matchParentSize())
        }
    }
}
