package com.spiderbiggen.manga.presentation.ui.manga.chapter.list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewDynamicColors
import androidx.compose.ui.tooling.preview.PreviewFontScale
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.dropUnlessStarted
import com.spiderbiggen.manga.domain.model.chapter.Chapter
import com.spiderbiggen.manga.domain.model.chapter.ChapterForOverview
import com.spiderbiggen.manga.domain.model.id.ChapterId
import com.spiderbiggen.manga.presentation.R
import com.spiderbiggen.manga.presentation.components.FavoriteToggle
import com.spiderbiggen.manga.presentation.components.LoadingSpinner
import com.spiderbiggen.manga.presentation.components.MangaScaffold
import com.spiderbiggen.manga.presentation.components.ReadStateCard
import com.spiderbiggen.manga.presentation.components.StickyTopEffect
import com.spiderbiggen.manga.presentation.components.pulltorefresh.PullToRefreshBox
import com.spiderbiggen.manga.presentation.components.rememberManualScrollState
import com.spiderbiggen.manga.presentation.components.section
import com.spiderbiggen.manga.presentation.components.topappbar.rememberTopAppBarState
import com.spiderbiggen.manga.presentation.theme.MangaReaderTheme
import com.spiderbiggen.manga.presentation.ui.manga.chapter.list.model.ChapterRowData
import kotlin.time.Clock.System.now
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

@Composable
fun ChapterListScreen(
    viewModel: MangaChapterListViewModel = hiltViewModel(),
    snackbarHostState: SnackbarHostState,
    onBackClick: () -> Unit,
    onChapterClick: (ChapterId) -> Unit,
) {
    LaunchedEffect(viewModel) {
        viewModel.snackbarFlow.collect {
            snackbarHostState.showSnackbar(it)
        }
    }
    val state by viewModel.state.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
    ChapterListScreen(
        state = state,
        snackbarHostState = snackbarHostState,
        isRefreshing = isRefreshing,
        onBackClick = onBackClick,
        onRefresh = viewModel::onRefresh,
        onToggleFavorite = viewModel::onToggleFavorite,
        onChapterClick = onChapterClick,
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
fun ChapterListScreen(
    state: MangaChapterScreenState,
    snackbarHostState: SnackbarHostState,
    isRefreshing: Boolean = false,
    onBackClick: () -> Unit = {},
    onRefresh: () -> Unit = {},
    onToggleFavorite: () -> Unit = {},
    onChapterClick: (ChapterId) -> Unit = {},
) {
    val lazyListState = rememberLazyListState()
    val isManuallyScrolled = rememberManualScrollState(lazyListState)
    val topAppBarState = rememberTopAppBarState()

    val readyState = state as? MangaChapterScreenState.Ready
    MangaScaffold(
        contentWindowInsets = WindowInsets.systemBars,
        topBar = {
            Box(
                Modifier
                    .onSizeChanged { topAppBarState.appBarHeight = it.height.toFloat() }
                    .background(MaterialTheme.colorScheme.background)
                    .windowInsetsPadding(TopAppBarDefaults.windowInsets),
            ) {
                TopAppBar(
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(painterResource(R.drawable.arrow_back), "Back")
                        }
                    },
                    title = { Text(readyState?.title ?: "Manga") },
                    actions = {
                        IconButton(onClick = onToggleFavorite) {
                            FavoriteToggle(isFavorite = readyState?.isFavorite == true)
                        }
                    },
                )
            }
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        },
        topBarOffset = { topAppBarState.appBarOffset.floatValue.toInt() },
    ) { scaffoldPadding ->
        when (state) {
            is MangaChapterScreenState.Loading,
            is MangaChapterScreenState.Error,
            -> LoadingSpinner(scaffoldPadding)

            is MangaChapterScreenState.Ready -> {
                PullToRefreshBox(
                    isRefreshing = isRefreshing,
                    onRefresh = onRefresh,
                    modifier = Modifier.fillMaxSize(),
                ) {
                    StickyTopEffect(
                        items = state.chapters,
                        listState = lazyListState,
                        isManuallyScrolled = isManuallyScrolled,
                    )
                    ChaptersList(
                        lazyListState = lazyListState,
                        chapters = state.chapters,
                        modifier = Modifier
                            .fillMaxSize()
                            .nestedScroll(topAppBarState.nestedScrollConnection),
                        contentPadding = scaffoldPadding,
                        onChapterClick = onChapterClick,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun ChaptersList(
    chapters: ImmutableList<ChapterRowData>,
    onChapterClick: (ChapterId) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    lazyListState: LazyListState = rememberLazyListState(),
) {
    val floatAnimationSpec = MaterialTheme.motionScheme.defaultSpatialSpec<Float>()
    val intOffsetAnimateSpec = MaterialTheme.motionScheme.defaultSpatialSpec<IntOffset>()
    LazyColumn(
        modifier = modifier,
        state = lazyListState,
        contentPadding = contentPadding + PaddingValues(horizontal = 8.dp),
        verticalArrangement = Arrangement.spacedBy(1.dp),
    ) {
        section(
            header = null,
            items = chapters,
            key = { item -> item.id.inner },
        ) { item, shape ->
            ChapterRow(
                item = item,
                navigateToChapter = onChapterClick,
                modifier = Modifier
                    .animateItem(
                        fadeInSpec = floatAnimationSpec,
                        placementSpec = intOffsetAnimateSpec,
                        fadeOutSpec = floatAnimationSpec,
                    )
                    .defaultMinSize(minHeight = 48.dp),
                shape = shape,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun ChapterRow(
    item: ChapterRowData,
    navigateToChapter: (ChapterId) -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = CardDefaults.elevatedShape,
) {
    ReadStateCard(
        isRead = item.isRead,
        onClick = dropUnlessStarted { navigateToChapter(item.id) },
        shape = shape,
        modifier = modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .heightIn(min = 48.dp)
                .padding(start = 8.dp, top = 8.dp, end = 16.dp, bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            NumberDisplay(item)
            Column(verticalArrangement = Arrangement.Center) {
                Text(
                    text = item.date,
                    style = when {
                        item.isRead -> MaterialTheme.typography.titleMedium
                        else -> MaterialTheme.typography.titleMediumEmphasized
                    },
                )
                item.title?.let {
                    Text(
                        text = it,
                        style = when {
                            item.isRead -> MaterialTheme.typography.bodyMedium
                            else -> MaterialTheme.typography.bodyMediumEmphasized
                        },
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun NumberDisplay(item: ChapterRowData, modifier: Modifier = Modifier) {
    val maxTextWidth = rememberMaxTextWidth(MaterialTheme.typography.titleLargeEmphasized)
    val padding = with(LocalDensity.current) { 3.sp.toDp() }
    Row(
        modifier = modifier.widthIn(min = maxTextWidth),
        horizontalArrangement = Arrangement.spacedBy(padding),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = item.index.toString(),
            style = when {
                item.isRead -> MaterialTheme.typography.titleLarge
                else -> MaterialTheme.typography.titleLargeEmphasized
            },
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxHeight()
                .alignBy { 0 },
        )
        item.subIndex?.let {
            Text(
                text = it.toString(),
                style = when {
                    item.isRead -> MaterialTheme.typography.titleMedium
                    else -> MaterialTheme.typography.titleMediumEmphasized
                },
                modifier = Modifier.alignBy { 0 },
            )
        }
    }
}

@Composable
private fun rememberMaxTextWidth(style: TextStyle): Dp {
    val measurer = rememberTextMeasurer()
    val density = LocalDensity.current
    return remember(style, density) {
        with(density) {
            measurer.measure("999", style).size.width.toDp()
        }
    }
}

@PreviewLightDark
@PreviewDynamicColors
@PreviewFontScale
@Composable
fun PreviewManga(@PreviewParameter(ChapterOverviewScreenStateProvider::class) state: MangaChapterScreenState) {
    val isRefreshing = remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    MangaReaderTheme {
        ChapterListScreen(
            state = state,
            snackbarHostState = snackbarHostState,
            isRefreshing = isRefreshing.value,
            onRefresh = {
                coroutineScope.launch {
                    isRefreshing.value = true
                    delay(2000)
                    isRefreshing.value = false
                }
            },
            onChapterClick = {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Clicked on chapter $it")
                }
            },
        )
    }
}

class ChapterOverviewScreenStateProvider : PreviewParameterProvider<MangaChapterScreenState.Ready> {
    override val values
        get() = sequenceOf(
            MangaChapterScreenState.Ready(
                title = "Heavenly Martial God",
                isFavorite = false,
                chapters = ChapterProvider.values.take(1).toImmutableList(),
            ),
            MangaChapterScreenState.Ready(
                title = "Heavenly Martial God",
                isFavorite = false,
                chapters = ChapterProvider.values.toImmutableList(),
            ),
            MangaChapterScreenState.Ready(
                title = "Heavenly Martial God",
                isFavorite = true,
                chapters = ChapterProvider.values.toImmutableList(),
            ),
        )
}

private object ChapterProvider {
    private val mapChapterRowData = MapChapterRowData()

    val values: Sequence<ChapterRowData>
        get() = sequenceOf(
            mapChapterRowData(
                ChapterForOverview(
                    chapter = Chapter(
                        id = ChapterId("000000"),
                        index = 30u,
                        title = null,
                        date = LocalDate.parse("2023-04-16"),
                        updatedAt = now(),
                    ),
                    isRead = false,
                ),
            ),
            mapChapterRowData(
                ChapterForOverview(
                    chapter = Chapter(
                        id = ChapterId("000001"),
                        index = 29u,
                        subIndex = 5u,
                        title = null,
                        date = LocalDate.parse("2023-04-12"),
                        updatedAt = now(),
                    ),
                    isRead = false,
                ),
            ),
            mapChapterRowData(
                ChapterForOverview(
                    chapter = Chapter(
                        id = ChapterId("000002"),
                        index = 29u,
                        subIndex = 4u,
                        title = null,
                        date = LocalDate.parse("2023-04-12"),
                        updatedAt = now(),
                    ),
                    isRead = true,
                ),
            ),
            mapChapterRowData(
                ChapterForOverview(
                    chapter = Chapter(
                        id = ChapterId("000003"),
                        index = 29u,
                        subIndex = 3u,
                        title = null,
                        date = LocalDate.parse("2023-04-12"),
                        updatedAt = now(),
                    ),
                    isRead = true,
                ),
            ),
            mapChapterRowData(
                ChapterForOverview(
                    chapter = Chapter(
                        id = ChapterId("000004"),
                        index = 29u,
                        subIndex = 2u,
                        title = null,
                        date = LocalDate.parse("2023-04-12"),
                        updatedAt = now(),
                    ),
                    isRead = true,
                ),
            ),
            mapChapterRowData(
                ChapterForOverview(
                    chapter = Chapter(
                        id = ChapterId("000005"),
                        index = 29u,
                        subIndex = 1u,
                        title = null,
                        date = LocalDate.parse("2023-04-12"),
                        updatedAt = now(),
                    ),
                    isRead = true,
                ),
            ),
            mapChapterRowData(
                ChapterForOverview(
                    chapter = Chapter(
                        id = ChapterId("000006"),
                        index = 29u,
                        title = null,
                        date = LocalDate.parse("2023-03-15"),
                        updatedAt = now(),
                    ),
                    isRead = false,
                ),
            ),
            mapChapterRowData(
                ChapterForOverview(
                    chapter = Chapter(
                        id = ChapterId("000007"),
                        index = 28u,
                        title = "Long title to make the title take two lines at least and just a bit more",
                        date = LocalDate.parse("2023-02-28"),
                        updatedAt = now(),
                    ),
                    isRead = false,
                ),
            ),
        )
}
