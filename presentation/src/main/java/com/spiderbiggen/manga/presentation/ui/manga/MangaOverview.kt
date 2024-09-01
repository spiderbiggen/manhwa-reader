package com.spiderbiggen.manga.presentation.ui.manga

import android.content.res.Configuration
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BugReport
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleStartEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.dropUnlessResumed
import androidx.lifecycle.coroutineScope
import coil3.ImageLoader
import coil3.SingletonImageLoader
import com.google.firebase.BuildConfig
import com.spiderbiggen.manga.domain.model.id.MangaId
import com.spiderbiggen.manga.presentation.components.ListImagePreloader
import com.spiderbiggen.manga.presentation.components.LoadingSpinner
import com.spiderbiggen.manga.presentation.components.MangaRow
import com.spiderbiggen.manga.presentation.components.StickyTopEffect
import com.spiderbiggen.manga.presentation.components.UpdatedListButton
import com.spiderbiggen.manga.presentation.components.rememberManualScrollState
import com.spiderbiggen.manga.presentation.theme.MangaReaderTheme
import com.spiderbiggen.manga.presentation.ui.manga.model.MangaViewData
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch

@Composable
fun MangaOverview(viewModel: MangaViewModel, imageLoader: ImageLoader, navigateToManga: (MangaId) -> Unit) {
    LifecycleStartEffect(viewModel) {
        val job = lifecycle.coroutineScope.launch {
            viewModel.collect()
        }
        onStopOrDispose {
            job.cancel()
        }
    }
    val state = viewModel.state.collectAsStateWithLifecycle()
    val updatingState = viewModel.updatingState.collectAsState()
    MangaReaderTheme {
        MangaOverview(
            state = state,
            imageLoader = imageLoader,
            refreshing = updatingState,
            onRefreshClicked = viewModel::onPullToRefresh,
            toggleFavoritesFilter = viewModel::toggleFavoritesOnly,
            toggleUnreadFilter = viewModel::toggleUnreadOnly,
            navigateToManga = navigateToManga,
            onClickFavorite = viewModel::onClickFavorite,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun MangaOverview(
    state: State<MangaScreenState>,
    refreshing: State<Boolean> = remember { mutableStateOf(false) },
    imageLoader: ImageLoader = SingletonImageLoader.get(LocalContext.current),
    onRefreshClicked: () -> Unit = {},
    toggleFavoritesFilter: () -> Unit = {},
    toggleUnreadFilter: () -> Unit = {},
    navigateToManga: (MangaId) -> Unit = {},
    onClickFavorite: (MangaId) -> Unit = {},
) {
    val scope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState()
    val manuallyScrolled = rememberManualScrollState(lazyListState)
    val topAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val topAppBarColors = TopAppBarDefaults.topAppBarColors()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Manga") },
                scrollBehavior = topAppBarScrollBehavior,
                actions = {
                    if (BuildConfig.DEBUG) {
                        IconButton(onClick = { throw Throwable() }) {
                            Icon(Icons.Rounded.BugReport, contentDescription = "Create a crash report (by crashing)")
                        }
                    }
                },
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
                            label = "AppBarContainer",
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
                                onClick = dropUnlessResumed {
                                    toggleFavoritesFilter()
                                },
                                label = { Text("Favorites") },
                            )
                            FilterChip(
                                selected = screenState.unreadOnly,
                                onClick = dropUnlessResumed {
                                    toggleUnreadFilter()
                                },
                                label = { Text("Unread") },
                            )
                        }
                        Box(
                            Modifier.weight(1f),
                            contentAlignment = Alignment.TopCenter,
                        ) {
                            StickyTopEffect(
                                items = screenState.manga,
                                listState = lazyListState,
                                manuallyScrolled = manuallyScrolled,
                            )
                            MangaList(
                                mangas = screenState.manga,
                                imageLoader = imageLoader,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
                                lazyListState = lazyListState,
                                navigateToManga = navigateToManga,
                                onClickFavorite = onClickFavorite,
                            )
                            UpdatedListButton(
                                collection = screenState.manga,
                                key = { it.id.inner },
                                listState = lazyListState,
                                modifier = Modifier.padding(top = 8.dp),
                                scope = scope,
                                manuallyScrolled = manuallyScrolled,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MangaList(
    mangas: ImmutableList<MangaViewData>,
    imageLoader: ImageLoader,
    modifier: Modifier = Modifier,
    lazyListState: LazyListState = rememberLazyListState(),
    navigateToManga: (MangaId) -> Unit = {},
    onClickFavorite: (MangaId) -> Unit = {},
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
        items(mangas, key = { it.id.inner }) { item ->
            MangaRow(
                manga = item,
                imageLoader = imageLoader,
                navigateToManga = navigateToManga,
                onClickFavorite = onClickFavorite,
                modifier = Modifier.animateItem(),
            )
        }
    }
}

@Preview("Light")
@Preview("Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
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
                manga = MangaProvider.values.toImmutableList(),
                favoritesOnly = true,
                unreadOnly = false,
            ),
        )
}

object MangaProvider {
    private val baseViewData = MangaViewData(
        source = "Asura",
        id = MangaId("7df204a8-2d37-42d1-a2e0-e795ae618388"),
        title = "Heavenly Martial God",
        coverImage = "https://www.asurascans.com/wp-content/uploads/2021/09/martialgod.jpg",
        status = "Ongoing",
        updatedAt = "2023-04-23",
        isFavorite = false,
        readAll = false,
    )

    val values
        get() = sequenceOf(
            baseViewData,
            baseViewData.copy(
                id = MangaId("2"),
                status = "Dropped",
                isFavorite = true,
                readAll = false,
            ),
            baseViewData.copy(
                id = MangaId("3"),
                isFavorite = true,
                readAll = true,
            ),
        )
}
