package com.spiderbiggen.manga.presentation.ui.manga.overview

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BugReport
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleStartEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.coroutineScope
import coil3.ImageLoader
import coil3.SingletonImageLoader
import com.google.firebase.BuildConfig
import com.spiderbiggen.manga.domain.model.id.MangaId
import com.spiderbiggen.manga.presentation.components.MangaRow
import com.spiderbiggen.manga.presentation.components.MangaScaffold
import com.spiderbiggen.manga.presentation.components.StickyTopEffect
import com.spiderbiggen.manga.presentation.components.rememberManualScrollState
import com.spiderbiggen.manga.presentation.components.scrollableFade
import com.spiderbiggen.manga.presentation.components.snackbar.SnackbarData
import com.spiderbiggen.manga.presentation.components.topappbar.rememberTopAppBarState
import com.spiderbiggen.manga.presentation.extensions.plus
import com.spiderbiggen.manga.presentation.theme.MangaReaderTheme
import com.spiderbiggen.manga.presentation.ui.manga.model.MangaScreenData
import com.spiderbiggen.manga.presentation.ui.manga.model.MangaScreenState
import com.spiderbiggen.manga.presentation.ui.manga.model.MangaViewData
import com.spiderbiggen.manga.presentation.ui.manga.overview.explore.ExploreViewModel
import com.spiderbiggen.manga.presentation.ui.manga.overview.favorites.MangaFavoritesViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch

@Composable
fun MangaOverview(
    viewModel: MangaFavoritesViewModel,
    imageLoader: ImageLoader,
    showSnackbar: suspend (SnackbarData) -> Unit,
    navigateToManga: (MangaId) -> Unit,
) {
    LaunchedEffect(viewModel, showSnackbar) {
        viewModel.snackbarFlow.collect {
            showSnackbar(it)
        }
    }
    LifecycleStartEffect(viewModel) {
        val job = lifecycle.coroutineScope.launch {
            viewModel.collect()
        }
        onStopOrDispose {
            job.cancel()
        }
    }
    val data by viewModel.state.collectAsStateWithLifecycle()
    val updatingState by viewModel.updatingState.collectAsState()

    MangaOverview(
        data = data,
        imageLoader = imageLoader,
        refreshing = updatingState,
        onToggleUnreadRequested = viewModel::onToggleUnread,
        onRefreshClicked = viewModel::onRefreshRequested,
        navigateToManga = navigateToManga,
        onClickFavorite = viewModel::onClickFavorite,
    )
}

@Composable
fun MangaOverview(
    viewModel: ExploreViewModel,
    imageLoader: ImageLoader,
    showSnackbar: suspend (SnackbarData) -> Unit,
    navigateToManga: (MangaId) -> Unit,
) {
    LaunchedEffect(viewModel, showSnackbar) {
        viewModel.snackbarFlow.collect {
            showSnackbar(it)
        }
    }
    LifecycleStartEffect(viewModel) {
        val job = lifecycle.coroutineScope.launch {
            viewModel.collect()
        }
        onStopOrDispose {
            job.cancel()
        }
    }
    val data by viewModel.state.collectAsStateWithLifecycle()
    val updatingState by viewModel.updatingState.collectAsState()

    MangaOverview(
        data = data,
        imageLoader = imageLoader,
        refreshing = updatingState,
        onToggleUnreadRequested = viewModel::onToggleUnread,
        onRefreshClicked = viewModel::onPullToRefresh,
        navigateToManga = navigateToManga,
        onClickFavorite = viewModel::onClickFavorite,
    )
}

@Composable
fun MangaOverview(
    data: MangaScreenData,
    refreshing: Boolean = false,
    imageLoader: ImageLoader = SingletonImageLoader.get(LocalContext.current),
    onToggleUnreadRequested: () -> Unit = {},
    onRefreshClicked: () -> Unit = {},
    navigateToManga: (MangaId) -> Unit = {},
    onClickFavorite: (MangaId) -> Unit = {},
) {
    when (data.state) {
        // TODO create error screen
        // TODO create loading shimmer
        is MangaScreenState.Error,
        is MangaScreenState.Loading,
        -> MangaOverviewContent(
            imageLoader = imageLoader,
            manga = persistentListOf(),
            unreadSelected = data.filterUnread,
            onToggleUnreadRequested = onToggleUnreadRequested,
            refreshing = refreshing,
            onRefreshRequested = onRefreshClicked,
            navigateToManga = navigateToManga,
            onClickFavorite = onClickFavorite,
        )

        is MangaScreenState.Ready -> MangaOverviewContent(
            imageLoader = imageLoader,
            manga = data.state.manga,
            unreadSelected = data.filterUnread,
            onToggleUnreadRequested = onToggleUnreadRequested,
            refreshing = refreshing,
            onRefreshRequested = onRefreshClicked,
            navigateToManga = navigateToManga,
            onClickFavorite = onClickFavorite,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MangaOverviewContent(
    imageLoader: ImageLoader,
    manga: ImmutableList<MangaViewData>,
    unreadSelected: Boolean,
    onToggleUnreadRequested: () -> Unit = {},
    refreshing: Boolean,
    onRefreshRequested: () -> Unit = {},
    navigateToManga: (MangaId) -> Unit = {},
    onClickFavorite: (MangaId) -> Unit = {},
) {
    val lazyListState = rememberLazyListState()
    val manuallyScrolled = rememberManualScrollState(lazyListState)
    val topAppBarState = rememberTopAppBarState()

    MangaScaffold(
        contentWindowInsets = WindowInsets.systemBars,
        topBar = {
            Column(
                Modifier
                    .onSizeChanged { topAppBarState.appBarHeight = it.height.toFloat() }
                    .background(MaterialTheme.colorScheme.background)
                    .windowInsetsPadding(TopAppBarDefaults.windowInsets),
            ) {
                TopAppBar(
                    title = { Text("Manga") },
                    actions = {
                        if (BuildConfig.DEBUG) {
                            IconButton(onClick = { throw Throwable() }) {
                                Icon(
                                    Icons.Rounded.BugReport,
                                    contentDescription = "Create a crash report (by crashing)",
                                )
                            }
                        }
                    },
                )
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    FilterChip(
                        selected = unreadSelected,
                        label = { Text("Unread") },
                        leadingIcon = {
                            AnimatedVisibility(unreadSelected) {
                                Icon(Icons.Rounded.Check, null)
                            }
                        },
                        onClick = onToggleUnreadRequested,
                    )
                }
            }
        },
        topBarOffset = { topAppBarState.appBarOffset.floatValue.toInt() },
    ) { scaffoldPadding ->
        val pullToRefreshState = rememberPullToRefreshState()
        PullToRefreshBox(
            isRefreshing = refreshing,
            onRefresh = onRefreshRequested,
            modifier = Modifier.fillMaxSize(),
            state = pullToRefreshState,
        ) {
            StickyTopEffect(
                items = manga,
                listState = lazyListState,
                manuallyScrolled = manuallyScrolled,
            )
            MangaList(
                mangas = manga,
                imageLoader = imageLoader,
                modifier = Modifier
                    .fillMaxSize()
                    .nestedScroll(topAppBarState.nestedScrollConnection)
                    .scrollableFade(
                        canScrollBackward = { lazyListState.canScrollBackward },
                        canScrollForward = { lazyListState.canScrollForward },
                    ),
                contentPadding = scaffoldPadding,
                lazyListState = lazyListState,
                navigateToManga = navigateToManga,
                onClickFavorite = onClickFavorite,
            )
        }
    }
}

@Composable
private fun MangaList(
    mangas: ImmutableList<MangaViewData>,
    imageLoader: ImageLoader,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    lazyListState: LazyListState = rememberLazyListState(),
    navigateToManga: (MangaId) -> Unit = {},
    onClickFavorite: (MangaId) -> Unit = {},
) {
    LazyColumn(
        modifier = modifier,
        state = lazyListState,
        contentPadding = contentPadding + PaddingValues(top = 8.dp, start = 8.dp, end = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
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
fun PreviewManga(@PreviewParameter(MangaOverviewScreenDataProvider::class) state: MangaScreenData) {
    MangaReaderTheme {
        MangaOverview(data = state)
    }
}

class MangaOverviewScreenDataProvider : PreviewParameterProvider<MangaScreenData> {
    override val values
        get() = sequenceOf(
            MangaScreenData(false, MangaScreenState.Loading),
            MangaScreenData(
                filterUnread = false,
                MangaScreenState.Ready(
                    manga = MangaProvider.values.toImmutableList(),
                ),
            ),
            MangaScreenData(
                filterUnread = true,
                MangaScreenState.Ready(
                    manga = MangaProvider.values.filter { !it.readAll }.toImmutableList(),
                ),
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
