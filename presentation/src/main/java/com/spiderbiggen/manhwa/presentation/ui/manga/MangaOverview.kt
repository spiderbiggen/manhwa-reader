package com.spiderbiggen.manhwa.presentation.ui.manga

import android.content.res.Configuration
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.tooling.preview.Wallpapers
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.spiderbiggen.manhwa.presentation.components.ListImagePreloader
import com.spiderbiggen.manhwa.presentation.components.MangaRow
import com.spiderbiggen.manhwa.presentation.theme.MangaReaderTheme
import com.spiderbiggen.manhwa.presentation.ui.manga.model.MangaViewData

@Composable
fun MangaOverview(
    navigateToManga: (String) -> Unit,
    viewModel: MangaViewModel = viewModel(),
    refreshing: State<Boolean> = remember { mutableStateOf(false) },
    onRefreshClicked: () -> Unit = {},
) {
    LaunchedEffect(null) {
        viewModel.collect()
    }
    val state = viewModel.state.collectAsStateWithLifecycle()
    MangaOverview(
        state = state,
        refreshing = refreshing,
        onRefreshClicked = onRefreshClicked,
        toggleFavoritesFilter = viewModel::toggleFavoritesOnly,
        toggleUnreadFilter = viewModel::toggleUnreadOnly,
        navigateToManga = navigateToManga,
        onClickFavorite = viewModel::onClickFavorite,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MangaOverview(
    state: State<MangaScreenState>,
    refreshing: State<Boolean> = remember { mutableStateOf(false) },
    onRefreshClicked: () -> Unit = {},
    toggleFavoritesFilter: () -> Unit = {},
    toggleUnreadFilter: () -> Unit = {},
    navigateToManga: (String) -> Unit = {},
    onClickFavorite: (String) -> Unit = {},
) {
    val topAppBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val pullToRefreshState = rememberPullToRefreshState()

    LaunchedEffect(refreshing.value) {
        if (refreshing.value) {
            pullToRefreshState.startRefresh()
        } else {
            pullToRefreshState.endRefresh()
        }
    }

    if (pullToRefreshState.isRefreshing) {
        LaunchedEffect(true) {
            onRefreshClicked()
        }
    }
    val scaleFraction = if (pullToRefreshState.isRefreshing) {
        1f
    } else {
        LinearOutSlowInEasing.transform(pullToRefreshState.progress).coerceIn(0f, 1f)
    }

    Scaffold(
        modifier = Modifier
            .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection)
            .nestedScroll(pullToRefreshState.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = { Text("Manga") },
                scrollBehavior = topAppBarScrollBehavior,
                colors = TopAppBarDefaults.topAppBarColors(
                    scrolledContainerColor = MaterialTheme.colorScheme.surface,
                ),
            )
        },
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.TopCenter,
        ) {
            when (val screenState = state.value) {
                is MangaScreenState.Error,
                is MangaScreenState.Loading,
                -> CircularProgressIndicator(
                    Modifier.align(Alignment.Center),
                )

                is MangaScreenState.Ready -> {
                    MangaList(
                        mangaGroups = screenState.manga,
                        favoritesOnly = screenState.favoritesOnly,
                        unReadOnly = screenState.unreadOnly,
                        toggleFavoritesFilter = toggleFavoritesFilter,
                        toggleUnreadFilter = toggleUnreadFilter,
                        navigateToManga = navigateToManga,
                        onClickFavorite = onClickFavorite,
                    )
                    PullToRefreshContainer(
                        state = pullToRefreshState,
                        modifier = Modifier.graphicsLayer(
                            scaleX = scaleFraction,
                            scaleY = scaleFraction,
                        ),
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalLayoutApi::class)
@Composable
private fun MangaList(
    mangaGroups: Map<String, List<MangaViewData>>,
    modifier: Modifier = Modifier,
    favoritesOnly: Boolean = false,
    unReadOnly: Boolean = false,
    lazyListState: LazyListState = rememberLazyListState(),
    toggleFavoritesFilter: () -> Unit = {},
    toggleUnreadFilter: () -> Unit = {},
    navigateToManga: (String) -> Unit = {},
    onClickFavorite: (String) -> Unit = {},
) {
    val images by remember(mangaGroups) {
        derivedStateOf {
            mangaGroups.flatMap { (_, manga) ->
                manga.map { it.coverImage }
            }
        }
    }

    ListImagePreloader(
        items = images,
        visibleCount = 7,
        preloadCount = 15,
        lazyListState = lazyListState,
    )
    LazyColumn(
        modifier = modifier,
        state = lazyListState,
    ) {
        stickyHeader {
            FlowRow(
                Modifier
                    .background(MaterialTheme.colorScheme.surface)
                    .fillParentMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start),
                verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top),
            ) {
                FilterChip(
                    selected = favoritesOnly,
                    onClick = toggleFavoritesFilter,
                    label = { Text("Favorites") },
                )
                FilterChip(
                    selected = unReadOnly,
                    onClick = toggleUnreadFilter,
                    label = { Text("Unread") },
                )
            }
        }
        mangaGroups.map { (header, mangas) ->
            item {
                Text(
                    header,
                    Modifier.padding(8.dp),
                    style = MaterialTheme.typography.titleMedium,
                )
            }
            items(
                mangas,
                key = { it.id },
            ) { item ->
                MangaRow(
                    manga = item,
                    navigateToManga = navigateToManga,
                    onClickFavorite = onClickFavorite,
                    modifier = Modifier.animateItemPlacement(),
                )
            }
        }
    }
}

@Preview("Light", apiLevel = 26)
@Preview("Dark", uiMode = Configuration.UI_MODE_NIGHT_YES, apiLevel = 26)
@Preview(
    "Light - Red",
    group = "dynamic",
    wallpaper = Wallpapers.RED_DOMINATED_EXAMPLE,
    apiLevel = 32,
)
@Preview(
    "Dark - Red",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    group = "dynamic",
    wallpaper = Wallpapers.RED_DOMINATED_EXAMPLE,
    apiLevel = 32,
)
@Preview(
    "Light - Blue",
    group = "dynamic",
    wallpaper = Wallpapers.BLUE_DOMINATED_EXAMPLE,
    apiLevel = 32,
)
@Preview(
    "Dark - Blue",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    group = "dynamic",
    wallpaper = Wallpapers.BLUE_DOMINATED_EXAMPLE,
    apiLevel = 32,
)
@Preview(
    "Light - Green",
    group = "dynamic",
    wallpaper = Wallpapers.GREEN_DOMINATED_EXAMPLE,
    apiLevel = 32,
)
@Preview(
    "Dark - Green",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    group = "dynamic",
    wallpaper = Wallpapers.GREEN_DOMINATED_EXAMPLE,
    apiLevel = 32,
)
@Preview(
    "Light - Yellow",
    group = "dynamic",
    wallpaper = Wallpapers.YELLOW_DOMINATED_EXAMPLE,
    apiLevel = 32,
)
@Preview(
    "Dark - Yellow",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    group = "dynamic",
    wallpaper = Wallpapers.YELLOW_DOMINATED_EXAMPLE,
    apiLevel = 32,
)
@Composable
fun PreviewManga(
    @PreviewParameter(MangaOverviewScreenStateProvider::class) state: MangaScreenState,
) {
    MangaReaderTheme {
        MangaOverview(
            state = remember { derivedStateOf { state } },
        )
    }
}

class MangaOverviewScreenStateProvider : PreviewParameterProvider<MangaScreenState> {
    override val values
        get() = sequenceOf(
            MangaScreenState.Loading,
//            MangaScreenState.Error("An error occurred"),
            MangaScreenState.Ready(
                manga = mapOf("Today" to MangaProvider.values.toList()),
                favoritesOnly = true,
                unreadOnly = false,
            ),
        )
}

object MangaProvider {
    val values
        get() = sequenceOf(
            MangaViewData(
                source = "Asura",
                id = "7df204a8-2d37-42d1-a2e0-e795ae618388",
                title = "Heavenly Martial God",
                coverImage = "https://www.asurascans.com/wp-content/uploads/2021/09/martialgod.jpg",
                status = "Ongoing",
                updatedAt = "2023-04-23",
                isFavorite = false,
                readAll = false,
            ),
            MangaViewData(
                source = "Asura",
                id = "7df204a8-2d37-42d1-a2e0-e795ae618389",
                title = "Heavenly Martial God",
                coverImage = "https://www.asurascans.com/wp-content/uploads/2021/09/martialgod.jpg",
                status = "Dropped",
                updatedAt = "2023-04-23",
                isFavorite = true,
                readAll = false,
            ),
            MangaViewData(
                source = "Asura",
                id = "7df204a8-2d37-42d1-a2e0-e795ae618310",
                title = "Heavenly Martial God",
                coverImage = "https://www.asurascans.com/wp-content/uploads/2021/09/martialgod.jpg",
                status = "Ongoing",
                updatedAt = "2023-04-23",
                isFavorite = true,
                readAll = true,
            ),
        )
}
