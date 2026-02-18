package com.spiderbiggen.manga.presentation.ui.manga.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.WavyProgressIndicatorDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.annotation.ExperimentalCoilApi
import coil3.asImage
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePreviewHandler
import coil3.compose.LocalAsyncImagePreviewHandler
import coil3.compose.LocalPlatformContext
import coil3.compose.rememberConstraintsSizeResolver
import com.spiderbiggen.manga.domain.model.id.MangaId
import com.spiderbiggen.manga.presentation.BuildConfig
import com.spiderbiggen.manga.presentation.R
import com.spiderbiggen.manga.presentation.components.PreloadImages
import com.spiderbiggen.manga.presentation.components.StickyTopEffect
import com.spiderbiggen.manga.presentation.components.animation.ExpressiveAnimatedVisibility
import com.spiderbiggen.manga.presentation.components.plus
import com.spiderbiggen.manga.presentation.components.pulltorefresh.PullToRefreshBox
import com.spiderbiggen.manga.presentation.components.rememberManualScrollState
import com.spiderbiggen.manga.presentation.components.section
import com.spiderbiggen.manga.presentation.components.topappbar.MangaTopAppBar
import com.spiderbiggen.manga.presentation.components.topappbar.scrollWithContentBehavior
import com.spiderbiggen.manga.presentation.theme.MangaReaderTheme
import com.spiderbiggen.manga.presentation.ui.manga.list.components.MangaRow
import com.spiderbiggen.manga.presentation.ui.manga.list.model.MangaScreenData
import com.spiderbiggen.manga.presentation.ui.manga.list.model.MangaScreenState
import com.spiderbiggen.manga.presentation.ui.manga.list.model.MangaViewData
import com.spiderbiggen.manga.presentation.ui.profile.state.ProfileState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

@Composable
fun MangaListScreen(
    viewModel: MangaListViewModel,
    snackbarHostState: SnackbarHostState,
    profileState: ProfileState,
    onProfileClick: () -> Unit,
    onMangaClick: (MangaId) -> Unit,
) {
    LaunchedEffect(viewModel, snackbarHostState) {
        viewModel.snackbarFlow.collect {
            snackbarHostState.showSnackbar(it)
        }
    }
    val state by viewModel.state.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()

    MangaListScreen(
        state = state,
        snackbarHostState = snackbarHostState,
        profileState = profileState,
        isRefreshing = isRefreshing,
        onRefresh = viewModel::onRefresh,
        onProfileClicked = onProfileClick,
        onToggleUnread = viewModel::onToggleUnread,
        onToggleFavorites = viewModel::onToggleFavorites,
        onMangaClick = onMangaClick,
        onFavoriteClick = viewModel::onFavoriteClick,
    )
}

@Composable
fun MangaListScreen(
    state: MangaScreenData,
    snackbarHostState: SnackbarHostState,
    profileState: ProfileState,
    isRefreshing: Boolean = false,
    onRefresh: () -> Unit = {},
    onProfileClicked: () -> Unit = {},
    onToggleUnread: () -> Unit = {},
    onToggleFavorites: () -> Unit = {},
    onMangaClick: (MangaId) -> Unit = {},
    onFavoriteClick: (MangaId) -> Unit = {},
) {
    val manga = (state.state as? MangaScreenState.Ready)?.manga ?: persistentListOf()
    MangaOverviewContent(
        snackbarHostState = snackbarHostState,
        profileState = profileState,
        manga = manga,
        isUnreadSelected = state.filterUnread,
        isFavoritesSelected = state.filterFavorites,
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        onProfileClicked = onProfileClicked,
        onToggleUnreadRequested = onToggleUnread,
        onToggleFavoritesRequested = onToggleFavorites,
        onMangaClick = onMangaClick,
        onFavoriteClick = onFavoriteClick,
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun MangaOverviewContent(
    snackbarHostState: SnackbarHostState,
    profileState: ProfileState,
    manga: ImmutableList<Pair<String, ImmutableList<MangaViewData>>>,
    isUnreadSelected: Boolean,
    isFavoritesSelected: Boolean,
    isRefreshing: Boolean,
    onRefresh: () -> Unit = {},
    onProfileClicked: () -> Unit = {},
    onToggleUnreadRequested: () -> Unit = {},
    onToggleFavoritesRequested: () -> Unit = {},
    onMangaClick: (MangaId) -> Unit = {},
    onFavoriteClick: (MangaId) -> Unit = {},
) {
    val lazyListState = rememberLazyListState()
    val manuallyScrolled = rememberManualScrollState(lazyListState)
    val topAppBarScrollBehavior = TopAppBarDefaults.scrollWithContentBehavior(
        canScroll = { lazyListState.canScrollForward || lazyListState.canScrollBackward },
    )

    Scaffold(
        topBar = {
            MangaTopAppBar(
                navigationIcon = {
                    IconButton(onClick = onProfileClicked) {
                        when (profileState) {
                            is ProfileState.Unauthenticated -> Icon(
                                painterResource(R.drawable.account_circle),
                                contentDescription = "Profile",
                            )

                            is ProfileState.Authenticated -> Box(contentAlignment = Alignment.Center) {
                                AsyncImage(
                                    model = profileState.avatarUrl,
                                    contentDescription = "Profile",
                                    contentScale = ContentScale.Crop,
                                    error = painterResource(R.drawable.account_circle),
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .size(24.dp),
                                )
                                ExpressiveAnimatedVisibility(
                                    profileState.refreshing,
                                    Modifier.size(WavyProgressIndicatorDefaults.CircularContainerSize),
                                ) {
                                    CircularWavyProgressIndicator()
                                }
                            }
                        }
                    }
                },
                title = { Text("Manga") },
                actions = {
                    if (BuildConfig.DEBUG) {
                        IconButton(onClick = { throw Throwable() }) {
                            Icon(
                                painter = painterResource(R.drawable.bug_report),
                                contentDescription = "Create a crash report (by crashing)",
                            )
                        }
                    }
                },
                scrollBehavior = topAppBarScrollBehavior,
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { contentPadding ->
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = onRefresh,
            topOffSet = {
                (topAppBarScrollBehavior.state.heightOffset - topAppBarScrollBehavior.state.heightOffsetLimit).toInt()
            },
        ) {
            StickyTopEffect(
                items = manga,
                listState = lazyListState,
                isManuallyScrolled = manuallyScrolled,
            )
            MangaList(
                mangas = manga,
                modifier = Modifier
                    .fillMaxSize()
                    .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
                contentPadding = contentPadding,
                lazyListState = lazyListState,
                onMangaClick = onMangaClick,
                onFavoriteClick = onFavoriteClick,
            )
        }
    }
}

@Composable
private fun CheckedFilterChip(selected: Boolean, label: @Composable () -> Unit, onClick: () -> Unit) {
    FilterChip(
        selected = selected,
        label = label,
        leadingIcon = if (selected) {
            {
                Icon(painterResource(R.drawable.check), null)
            }
        } else {
            null
        },
        onClick = onClick,
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun MangaList(
    mangas: ImmutableList<Pair<String, ImmutableList<MangaViewData>>>,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    lazyListState: LazyListState = rememberLazyListState(),
    onMangaClick: (MangaId) -> Unit = {},
    onFavoriteClick: (MangaId) -> Unit = {},
) {
    val floatAnimationSpec = MaterialTheme.motionScheme.defaultEffectsSpec<Float>()
    val intOffsetAnimateSpec = MaterialTheme.motionScheme.defaultSpatialSpec<IntOffset>()

    val coverSizeResolver = rememberConstraintsSizeResolver()
    val allImages = remember(mangas) {
        mangas.flatMap { (_, mangas) -> mangas.map { it.coverImage } }.toImmutableList()
    }
    PreloadImages(
        lazyListState = lazyListState,
        items = allImages,
        sizeResolver = { coverSizeResolver },
        preloadCount = 15,
    )

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
                key = { it.id.value },
            ) { item, shape ->
                MangaRow(
                    manga = item,
                    onMangaClick = onMangaClick,
                    onMangaFavoriteToggleClick = onFavoriteClick,
                    modifier = Modifier.animateItem(
                        fadeInSpec = floatAnimationSpec,
                        placementSpec = intOffsetAnimateSpec,
                        fadeOutSpec = floatAnimationSpec,
                    ),
                    shape = shape,
                    coverSizeResolver = coverSizeResolver,
                )
            }
        }
    }
}

@OptIn(ExperimentalCoilApi::class)
@PreviewLightDark
@PreviewDynamicColors
@PreviewFontScale
@PreviewScreenSizes
@Composable
fun PreviewManga(@PreviewParameter(MangaOverviewScreenDataProvider::class) state: MangaScreenData) {
    val context = LocalPlatformContext.current
    val previewHandler = AsyncImagePreviewHandler {
        ResourcesCompat.getDrawable(context.resources, R.mipmap.preview_cover_placeholder, null)!!.asImage()
    }
    val snackbarHostState = remember { SnackbarHostState() }
    CompositionLocalProvider(LocalAsyncImagePreviewHandler provides previewHandler) {
        MangaReaderTheme {
            MangaListScreen(
                state = state,
                snackbarHostState = snackbarHostState,
                profileState = ProfileState.Unauthenticated,
            )
        }
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
