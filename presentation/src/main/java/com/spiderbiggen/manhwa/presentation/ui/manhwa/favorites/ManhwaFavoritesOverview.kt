package com.spiderbiggen.manhwa.presentation.ui.manhwa.favorites

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.tooling.preview.Wallpapers
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.spiderbiggen.manhwa.presentation.components.ManhwaRow
import com.spiderbiggen.manhwa.presentation.model.ManhwaViewData
import com.spiderbiggen.manhwa.presentation.theme.ManhwaReaderTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManhwaFavoritesOverview(
    navigateToManhwa: (String) -> Unit,
    navigateToOverview: () -> Unit,
    navigateToDropped: () -> Unit,
    viewModel: ManhwaFavoritesViewModel = viewModel()
) {
    LaunchedEffect(null) {
        viewModel.collect()
    }

    val state by viewModel.state.collectAsStateWithLifecycle()
    val lazyListState = rememberLazyListState()
    val topAppBarState = rememberTopAppBarState()
    ManhwaFavoritesOverview(
        navigateToManhwa = navigateToManhwa,
        navigateToOverview = navigateToOverview,
        navigateToDropped = navigateToDropped,
        state = state,
        lazyListState = lazyListState,
        topAppBarState = topAppBarState
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManhwaFavoritesOverview(
    navigateToManhwa: (String) -> Unit,
    navigateToOverview: () -> Unit,
    navigateToDropped: () -> Unit,
    state: ManhwaFavoritesScreenState,
    lazyListState: LazyListState = rememberLazyListState(),
    topAppBarState: TopAppBarState = rememberTopAppBarState(),
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(topAppBarState)
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Favorites") },
                scrollBehavior = scrollBehavior,
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    false,
                    navigateToOverview,
                    icon = {
                        Icon(Icons.Rounded.Search, null)
                    }
                )
                Spacer(modifier = Modifier.weight(1f))
                NavigationBarItem(
                    true,
                    {},
                    icon = {
                        Icon(Icons.Rounded.Favorite, null)
                    }
                )
                NavigationBarItem(
                    false,
                    navigateToDropped,
                    icon = {
                        Icon(Icons.Rounded.Lock, null)
                    }
                )
            }
        }
    ) { padding ->
        when (state) {
            is ManhwaFavoritesScreenState.Error,
            ManhwaFavoritesScreenState.Loading -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }

            is ManhwaFavoritesScreenState.Ready -> {
                LazyColumn(
                    Modifier
                        .padding(padding)
                        .nestedScroll(scrollBehavior.nestedScrollConnection),
                    state = lazyListState,
                ) {
                    items(state.manhwa, key = { it.id }) { manhwa ->
                        ManhwaRow(manhwa, navigateToManhwa)
                    }
                }
            }
        }
    }
}

@Preview("Light")
@Preview("Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview("Light - Red", group = "dynamic", wallpaper = Wallpapers.RED_DOMINATED_EXAMPLE)
@Preview(
    "Dark - Red",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    group = "dynamic",
    wallpaper = Wallpapers.RED_DOMINATED_EXAMPLE
)
@Preview("Light - Blue", group = "dynamic", wallpaper = Wallpapers.BLUE_DOMINATED_EXAMPLE)
@Preview(
    "Dark - Blue",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    group = "dynamic",
    wallpaper = Wallpapers.BLUE_DOMINATED_EXAMPLE
)
@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun PreviewManhwa(@PreviewParameter(ManhwaOverviewScreenStateProvider::class) state: ManhwaFavoritesScreenState) {
    ManhwaReaderTheme {
        ManhwaFavoritesOverview({}, {}, {}, state = state)
    }
}

class ManhwaOverviewScreenStateProvider : PreviewParameterProvider<ManhwaFavoritesScreenState> {
    override val values
        get() = sequenceOf(
            ManhwaFavoritesScreenState.Loading,
            ManhwaFavoritesScreenState.Error("An error occurred"),
            ManhwaFavoritesScreenState.Ready(ManhwaProvider.values.toList()),
        )
}

object ManhwaProvider {
    val values
        get() = sequenceOf(
            ManhwaViewData(
                source = "Asura",
                id = "7df204a8-2d37-42d1-a2e0-e795ae618388",
                title = "Heavenly Martial God",
                coverImage = "https://www.asurascans.com/wp-content/uploads/2021/09/martialgod.jpg",
                status = "Ongoing",
                updatedAt = "2023-04-34",
                isFavorite = true
            ),
            ManhwaViewData(
                source = "Asura",
                id = "7df204a8-2d37-42d1-a2e0-e795ae618388",
                title = "Heavenly Martial God",
                coverImage = "https://www.asurascans.com/wp-content/uploads/2021/09/martialgod.jpg",
                status = "Dropped",
                updatedAt = "2023-04-34",
                isFavorite = true
            )
        )
}
