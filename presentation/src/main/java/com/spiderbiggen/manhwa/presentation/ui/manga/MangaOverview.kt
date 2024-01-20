package com.spiderbiggen.manhwa.presentation.ui.manga

import android.content.res.Configuration
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.TopAppBarState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
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
import com.spiderbiggen.manhwa.presentation.model.MangaViewData
import com.spiderbiggen.manhwa.presentation.theme.MangaReaderTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MangaOverview(
    navigateToManga: (String) -> Unit,
    viewModel: MangaViewModel = viewModel()
) {
    LaunchedEffect(null) {
        viewModel.collect()
    }
    val scope = rememberCoroutineScope()
    val refreshingState by viewModel.updatingState.collectAsStateWithLifecycle(false)
    val state by viewModel.state.collectAsStateWithLifecycle()
    val topAppBarState = rememberTopAppBarState()
    MangaOverview(
        navigateToManga = navigateToManga,
        state = state,
        refreshing = refreshingState,
        onRefreshClicked = viewModel::onClickRefresh,
        topAppBarState = topAppBarState,
        scope = scope,
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun MangaOverview(
    navigateToManga: (String) -> Unit,
    state: MangaScreenState,
    refreshing: Boolean = false,
    onRefreshClicked: () -> Unit = {},
    topAppBarState: TopAppBarState = rememberTopAppBarState(),
    scope: CoroutineScope = rememberCoroutineScope(),
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(topAppBarState)
    val pagerState = rememberPagerState(initialPage = 1, pageCount = { 2 })
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Manga") },
                scrollBehavior = scrollBehavior,
                actions = {
                    val rotation = if (refreshing) {
                        val infiniteTransition = rememberInfiniteTransition(
                            label = "Refresh indicator animation"
                        )
                        infiniteTransition.animateFloat(
                            label = "Refresh Rotation",
                            initialValue = 0f,
                            targetValue = 360f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(1000, easing = LinearEasing),
                                repeatMode = RepeatMode.Restart
                            )
                        )
                    } else {
                        remember { mutableFloatStateOf(0f) }
                    }
                    IconButton(onRefreshClicked) {
                        Icon(
                            imageVector = Icons.Outlined.Refresh,
                            contentDescription = "Refresh",
                            modifier = Modifier.rotate(rotation.value)
                        )
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    pagerState.currentPage == 0,
                    { scope.launch { pagerState.animateScrollToPage(0) } },
                    icon = {
                        Icon(Icons.Rounded.Search, null)
                    }
                )
                NavigationBarItem(
                    pagerState.currentPage == 1,
                    { scope.launch { pagerState.animateScrollToPage(1) } },
                    icon = {
                        Icon(Icons.Rounded.Favorite, null)
                    }
                )
            }
        }
    ) { padding ->
        when (state) {
            is MangaScreenState.Error,
            is MangaScreenState.Loading -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }

            is MangaScreenState.Ready -> {
                HorizontalPager(state = pagerState) { index ->
                    val mangas =
                        remember(key1 = state.manga) { state.manga.filter { index == 0 || it.isFavorite } }
                    MangaList(
                        mangas = mangas,
                        padding = padding,
                        navigateToManga = navigateToManga,
                        scrollBehavior = scrollBehavior
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun MangaList(
    mangas: List<MangaViewData>,
    padding: PaddingValues,
    navigateToManga: (String) -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
    lazyListState: LazyListState = rememberLazyListState(),
) {
    val images by remember { derivedStateOf { mangas.map { it.coverImage } } }
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
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item(key = "header", contentType = "header") {
            Text(text = "Header")
        }
        items(
            items = mangas,
            key = { item -> item.id },
        ) { item ->
            MangaRow(
                manga = item,
                navigateToManga = navigateToManga,
                modifier = Modifier
                    .animateItemPlacement()
            )
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
fun PreviewManga(@PreviewParameter(MangaOverviewScreenStateProvider::class) state: MangaScreenState) {
    MangaReaderTheme {
        MangaOverview({}, state = state)
    }
}

class MangaOverviewScreenStateProvider : PreviewParameterProvider<MangaScreenState> {
    override val values
        get() = sequenceOf(
            MangaScreenState.Loading,
            MangaScreenState.Error("An error occurred"),
            MangaScreenState.Ready(MangaProvider.values.take(2).toList()),
            MangaScreenState.Ready(
                MangaProvider.values.toList()
            )
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
            )
        )
}
