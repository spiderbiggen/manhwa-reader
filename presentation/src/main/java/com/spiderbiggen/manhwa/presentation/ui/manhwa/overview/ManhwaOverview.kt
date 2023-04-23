package com.spiderbiggen.manhwa.presentation.ui.manhwa.overview

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.tooling.preview.Wallpapers
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.spiderbiggen.manhwa.presentation.components.ListImagePreloader
import com.spiderbiggen.manhwa.presentation.components.ManhwaRow
import com.spiderbiggen.manhwa.presentation.model.ManhwaViewData
import com.spiderbiggen.manhwa.presentation.theme.ManhwaReaderTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManhwaOverview(
    navigateToManhwa: (String) -> Unit,
    navigateToFavorites: () -> Unit,
    navigateToDropped: () -> Unit,
    viewModel: ManhwaViewModel = viewModel()
) {
    LaunchedEffect(null) {
        viewModel.collect()
    }

    val state by viewModel.state.collectAsStateWithLifecycle()
    val lazyListState = rememberLazyListState()
    val topAppBarState = rememberTopAppBarState()
    ManhwaOverview(
        navigateToManhwa = navigateToManhwa,
        navigateToFavorites = navigateToFavorites,
        navigateToDropped = navigateToDropped,
        state = state,
        lazyListState = lazyListState,
        topAppBarState = topAppBarState
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManhwaOverview(
    navigateToManhwa: (String) -> Unit,
    navigateToFavorites: () -> Unit,
    navigateToDropped: () -> Unit,
    state: ManhwaScreenState,
    lazyListState: LazyListState = rememberLazyListState(),
    topAppBarState: TopAppBarState = rememberTopAppBarState(),
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(topAppBarState)
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Manhwa") },
                scrollBehavior = scrollBehavior,
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    true,
                    {},
                    icon = {
                        Icon(Icons.Rounded.Search, null)
                    }
                )
                NavigationBarItem(
                    false,
                    navigateToFavorites,
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
            is ManhwaScreenState.Error,
            ManhwaScreenState.Loading -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }

            is ManhwaScreenState.Ready -> {
                val images by remember { derivedStateOf { state.manhwa.map { it.coverImage } } }
                ListImagePreloader(
                    items = images,
                    visibleCount = 7,
                    preloadCount = 15,
                    lazyListState = lazyListState
                )
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
fun PreviewManhwa(@PreviewParameter(ManhwaOverviewScreenStateProvider::class) state: ManhwaScreenState) {
    ManhwaReaderTheme {
        ManhwaOverview({}, {}, {}, state = state)
    }
}

class ManhwaOverviewScreenStateProvider : PreviewParameterProvider<ManhwaScreenState> {
    override val values
        get() = sequenceOf(
            ManhwaScreenState.Loading,
            ManhwaScreenState.Error("An error occurred"),
            ManhwaScreenState.Ready(ManhwaProvider.values.take(2).toList()),
            ManhwaScreenState.Ready(
                ManhwaProvider.values.toList()
            )
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
                updatedAt = "2023-04-23",
                isFavorite = false
            ),
            ManhwaViewData(
                source = "Asura",
                id = "7df204a8-2d37-42d1-a2e0-e795ae618389",
                title = "Heavenly Martial God",
                coverImage = "https://www.asurascans.com/wp-content/uploads/2021/09/martialgod.jpg",
                status = "Dropped",
                updatedAt = null,
                isFavorite = true
            )
        )
}
