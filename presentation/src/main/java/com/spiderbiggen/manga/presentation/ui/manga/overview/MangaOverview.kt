package com.spiderbiggen.manga.presentation.ui.manga.overview

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.compose.AsyncImage
import com.spiderbiggen.manga.domain.model.id.MangaId
import com.spiderbiggen.manga.presentation.BuildConfig
import com.spiderbiggen.manga.presentation.R
import com.spiderbiggen.manga.presentation.components.MangaRow
import com.spiderbiggen.manga.presentation.components.MangaScaffold
import com.spiderbiggen.manga.presentation.components.StickyTopEffect
import com.spiderbiggen.manga.presentation.components.pulltorefresh.PullToRefreshBox
import com.spiderbiggen.manga.presentation.components.rememberManualScrollState
import com.spiderbiggen.manga.presentation.components.section
import com.spiderbiggen.manga.presentation.components.snackbar.SnackbarData
import com.spiderbiggen.manga.presentation.components.topappbar.rememberTopAppBarState
import com.spiderbiggen.manga.presentation.theme.MangaReaderTheme
import com.spiderbiggen.manga.presentation.ui.manga.model.MangaScreenData
import com.spiderbiggen.manga.presentation.ui.manga.model.MangaScreenState
import com.spiderbiggen.manga.presentation.ui.manga.model.MangaViewData
import com.spiderbiggen.manga.presentation.ui.profile.state.ProfileState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

@Composable
fun MangaOverview(
    viewModel: MangaOverviewViewModel = hiltViewModel(),
    profileState: ProfileState,
    imageLoader: ImageLoader,
    showSnackbar: suspend (SnackbarData) -> Unit,
    navigateToProfile: () -> Unit,
    navigateToManga: (MangaId) -> Unit,
) {
    val showSnackbar by rememberUpdatedState(showSnackbar)
    LaunchedEffect(viewModel) {
        viewModel.snackbarFlow.collect {
            showSnackbar(it)
        }
    }
    val data by viewModel.state.collectAsStateWithLifecycle()
    val updatingState by viewModel.updatingState.collectAsStateWithLifecycle()

    MangaOverview(
        data = data,
        profileState = profileState,
        imageLoader = imageLoader,
        refreshing = updatingState,
        onProfileClicked = navigateToProfile,
        onToggleUnreadRequested = viewModel::onToggleUnread,
        onToggleFavoritesRequested = viewModel::onToggleFavorites,
        onRefreshClicked = viewModel::onPullToRefresh,
        navigateToManga = navigateToManga,
        onClickFavorite = viewModel::onClickFavorite,
    )
}

@Composable
fun MangaOverview(
    data: MangaScreenData,
    profileState: ProfileState,
    refreshing: Boolean = false,
    imageLoader: ImageLoader = SingletonImageLoader.get(LocalContext.current),
    onProfileClicked: () -> Unit = {},
    onToggleUnreadRequested: () -> Unit = {},
    onToggleFavoritesRequested: () -> Unit = {},
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
            profileState = profileState,
            manga = persistentListOf(),
            unreadSelected = data.filterUnread,
            favoritesSelected = data.filterFavorites,
            onProfileClicked = onProfileClicked,
            onToggleUnreadRequested = onToggleUnreadRequested,
            onToggleFavoritesRequested = onToggleFavoritesRequested,
            refreshing = refreshing,
            onRefreshRequested = onRefreshClicked,
            navigateToManga = navigateToManga,
            onClickFavorite = onClickFavorite,
        )

        is MangaScreenState.Ready -> MangaOverviewContent(
            imageLoader = imageLoader,
            profileState = profileState,
            manga = data.state.manga,
            unreadSelected = data.filterUnread,
            favoritesSelected = data.filterFavorites,
            onProfileClicked = onProfileClicked,
            onToggleUnreadRequested = onToggleUnreadRequested,
            onToggleFavoritesRequested = onToggleFavoritesRequested,
            refreshing = refreshing,
            onRefreshRequested = onRefreshClicked,
            navigateToManga = navigateToManga,
            onClickFavorite = onClickFavorite,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun MangaOverviewContent(
    imageLoader: ImageLoader,
    profileState: ProfileState,
    manga: ImmutableList<Pair<String, ImmutableList<MangaViewData>>>,
    unreadSelected: Boolean,
    favoritesSelected: Boolean,
    onProfileClicked: () -> Unit = {},
    onToggleUnreadRequested: () -> Unit = {},
    onToggleFavoritesRequested: () -> Unit = {},
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
                    navigationIcon = {
                        IconButton(onClick = onProfileClicked) {
                            when (profileState) {
                                is ProfileState.Unauthenticated -> Icon(
                                    painterResource(R.drawable.account_circle),
                                    contentDescription = "Profile",
                                )

                                is ProfileState.Authenticated -> AsyncImage(
                                    model = profileState.avatarUrl,
                                    contentDescription = "Profile",
                                    contentScale = ContentScale.Crop,
                                    error = painterResource(R.drawable.account_circle),
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape),
                                )
                            }
                        }
                    },
                    title = { Text("Manga") },
                    actions = {
                        if (BuildConfig.DEBUG) {
                            IconButton(onClick = { throw Throwable() }) {
                                Icon(
                                    painterResource(R.drawable.bug_report),
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
                        selected = favoritesSelected,
                        label = { Text("Favorites") },
                        leadingIcon = if (favoritesSelected) {
                            {
                                Icon(painterResource(R.drawable.check), null)
                            }
                        } else {
                            null
                        },
                        onClick = onToggleFavoritesRequested,
                    )
                    FilterChip(
                        selected = unreadSelected,
                        label = { Text("Unread") },
                        leadingIcon = if (unreadSelected) {
                            {
                                Icon(painterResource(R.drawable.check), null)
                            }
                        } else {
                            null
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
                    .nestedScroll(topAppBarState.nestedScrollConnection),
                contentPadding = scaffoldPadding,
                lazyListState = lazyListState,
                navigateToManga = navigateToManga,
                onClickFavorite = onClickFavorite,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun MangaList(
    mangas: ImmutableList<Pair<String, ImmutableList<MangaViewData>>>,
    imageLoader: ImageLoader,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    lazyListState: LazyListState = rememberLazyListState(),
    navigateToManga: (MangaId) -> Unit = {},
    onClickFavorite: (MangaId) -> Unit = {},
) {
    val floatAnimationSpec = MaterialTheme.motionScheme.defaultEffectsSpec<Float>()
    val intOffsetAnimateSpec = MaterialTheme.motionScheme.defaultSpatialSpec<IntOffset>()

    LazyColumn(
        modifier = modifier,
        state = lazyListState,
        contentPadding = contentPadding + PaddingValues(all = 8.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        mangas.forEach { (key, values) ->
            section(
                header = key,
                items = values,
                key = { it.id.inner },
            ) { item, shape ->
                MangaRow(
                    manga = item,
                    imageLoader = imageLoader,
                    navigateToManga = navigateToManga,
                    onClickFavorite = onClickFavorite,
                    modifier = Modifier.animateItem(
                        fadeInSpec = floatAnimationSpec,
                        placementSpec = intOffsetAnimateSpec,
                        fadeOutSpec = floatAnimationSpec,
                    ),
                    shape = shape,
                )
            }
        }
    }
}

@Preview("Light")
@Preview("Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewManga(@PreviewParameter(MangaOverviewScreenDataProvider::class) state: MangaScreenData) {
    MangaReaderTheme {
        MangaOverview(data = state, profileState = ProfileState.Unauthenticated)
    }
}

class MangaOverviewScreenDataProvider : PreviewParameterProvider<MangaScreenData> {
    override val values
        get() = sequenceOf(
            MangaScreenData(),
            MangaScreenData(
                state = MangaScreenState.Ready(
                    manga = persistentListOf(
                        "header" to MangaProvider.values.toImmutableList(),
                    ),
                ),
            ),
            MangaScreenData(
                filterUnread = true,
                state = MangaScreenState.Ready(
                    manga = persistentListOf(
                        "header" to MangaProvider.values.filter { !it.isRead }.toImmutableList(),
                    ),
                ),
            ),
            MangaScreenData(
                filterFavorites = true,
                state = MangaScreenState.Ready(
                    manga = persistentListOf(
                        "header" to MangaProvider.values.filter { it.isFavorite }.toImmutableList(),
                    ),
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
        isRead = false,
    )

    val values
        get() = sequenceOf(
            baseViewData,
            baseViewData.copy(
                id = MangaId("2"),
                status = "Dropped",
                isFavorite = true,
                isRead = false,
            ),
            baseViewData.copy(
                id = MangaId("3"),
                isFavorite = true,
                isRead = true,
            ),
        )
}
