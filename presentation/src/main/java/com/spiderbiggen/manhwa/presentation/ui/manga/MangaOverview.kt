package com.spiderbiggen.manhwa.presentation.ui.manga

import android.content.res.Configuration
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.tooling.preview.Wallpapers
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.spiderbiggen.manhwa.presentation.components.ListImagePreloader
import com.spiderbiggen.manhwa.presentation.components.LoadingSpinner
import com.spiderbiggen.manhwa.presentation.components.MangaRow
import com.spiderbiggen.manhwa.presentation.components.StickyTopEffect
import com.spiderbiggen.manhwa.presentation.theme.MangaReaderTheme
import com.spiderbiggen.manhwa.presentation.ui.manga.model.MangaViewData
import kotlinx.coroutines.launch

@Composable
fun MangaOverview(navigateToManga: (String) -> Unit, viewModel: MangaViewModel = viewModel()) {
    LaunchedEffect(null) {
        viewModel.collect()
    }
    val state = viewModel.state.collectAsStateWithLifecycle()
    val updatingState = viewModel.updatingState.collectAsState()
    MangaOverview(
        state = state,
        refreshing = updatingState,
        onRefreshClicked = viewModel::onClickRefresh,
        toggleFavoritesFilter = viewModel::toggleFavoritesOnly,
        toggleUnreadFilter = viewModel::toggleUnreadOnly,
        navigateToManga = navigateToManga,
        onClickFavorite = viewModel::onClickFavorite,
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
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
    val scope = rememberCoroutineScope()
    val topAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val topAppBarColors = TopAppBarDefaults.topAppBarColors()

    Scaffold(
        modifier = Modifier.nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = { Text("Manga") },
                scrollBehavior = topAppBarScrollBehavior,
            )
        },
    ) { padding ->
        when (val screenState = state.value) {
            is MangaScreenState.Error,
            is MangaScreenState.Loading,
            -> LoadingSpinner(padding)

            is MangaScreenState.Ready -> {
                PullToRefreshBox(
                    refreshing.value,
                    onRefreshClicked,
                    Modifier
                        .fillMaxSize()
                        .padding(padding),
                ) {
                    Column(Modifier.fillMaxSize()) {
                        val lazyListState = rememberLazyListState()
                        // Obtain the container color from the TopAppBarColors using the `overlapFraction`. This
                        // ensures that the colors will adjust whether the app bar behavior is pinned or scrolled.
                        // This may potentially animate or interpolate a transition between the container-color and the
                        // container's scrolled-color according to the app bar's scroll state.
                        val colorTransitionFraction by remember {
                            // derivedStateOf to prevent redundant recompositions when the content scrolls.
                            derivedStateOf {
                                val overlappingFraction = topAppBarScrollBehavior.state.overlappedFraction
                                if (overlappingFraction > 0.01f) 1f else 0f
                            }
                        }

                        val appBarContainerColor by animateColorAsState(
                            targetValue = lerp(
                                topAppBarColors.containerColor,
                                topAppBarColors.scrolledContainerColor,
                                FastOutLinearInEasing.transform(colorTransitionFraction),
                            ),
                            animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                        )
                        FlowRow(
                            Modifier
                                .background(appBarContainerColor)
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .padding(bottom = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start),
                            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top),
                        ) {
                            FilterChip(
                                selected = screenState.favoritesOnly,
                                onClick = {
                                    toggleFavoritesFilter()
                                    scope.launch {
                                        lazyListState.requestScrollToItem(0)
                                    }
                                },
                                label = { Text("Favorites") },
                            )
                            FilterChip(
                                selected = screenState.unreadOnly,
                                onClick = {
                                    toggleUnreadFilter()
                                    scope.launch {
                                        lazyListState.requestScrollToItem(0)
                                    }
                                },
                                label = { Text("Unread") },
                            )
                        }

                        StickyTopEffect(screenState.manga, lazyListState)
                        MangaList(
                            modifier = Modifier.weight(1f),
                            mangas = screenState.manga,
                            lazyListState = lazyListState,
                            navigateToManga = navigateToManga,
                            onClickFavorite = onClickFavorite,
                        )
                    }
                }
            }
        }
    }
}

@Stable
@Composable
@OptIn(ExperimentalFoundationApi::class, ExperimentalLayoutApi::class)
private fun MangaList(
    mangas: List<MangaViewData>,
    modifier: Modifier = Modifier,
    lazyListState: LazyListState = rememberLazyListState(),
    navigateToManga: (String) -> Unit = {},
    onClickFavorite: (String) -> Unit = {},
) {
    val images by remember(mangas) {
        derivedStateOf {
            mangas.map { it.coverImage }
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
        items(mangas, key = { it.id }) { item ->
            MangaRow(
                manga = item,
                navigateToManga = navigateToManga,
                onClickFavorite = onClickFavorite,
                modifier = Modifier.animateItem(),
            )
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
fun PreviewManga(@PreviewParameter(MangaOverviewScreenStateProvider::class) state: MangaScreenState) {
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
            MangaScreenState.Ready(
                manga = MangaProvider.values.toList(),
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
