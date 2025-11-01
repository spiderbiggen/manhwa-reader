package com.spiderbiggen.manga.presentation.ui.manga.chapter.overview

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.dropUnlessStarted
import com.spiderbiggen.manga.domain.model.chapter.Chapter
import com.spiderbiggen.manga.domain.model.chapter.ChapterForOverview
import com.spiderbiggen.manga.domain.model.id.ChapterId
import com.spiderbiggen.manga.presentation.components.LoadingSpinner
import com.spiderbiggen.manga.presentation.components.MangaScaffold
import com.spiderbiggen.manga.presentation.components.ReadStateCard
import com.spiderbiggen.manga.presentation.components.StickyTopEffect
import com.spiderbiggen.manga.presentation.components.pulltorefresh.PullToRefreshBox
import com.spiderbiggen.manga.presentation.components.rememberManualScrollState
import com.spiderbiggen.manga.presentation.components.scrollableFade
import com.spiderbiggen.manga.presentation.components.section
import com.spiderbiggen.manga.presentation.components.snackbar.SnackbarData
import com.spiderbiggen.manga.presentation.components.topappbar.rememberTopAppBarState
import com.spiderbiggen.manga.presentation.theme.FontFamilies
import com.spiderbiggen.manga.presentation.theme.MangaReaderTheme
import com.spiderbiggen.manga.presentation.ui.manga.chapter.overview.model.ChapterRowData
import com.spiderbiggen.manga.presentation.ui.manga.chapter.overview.usecase.MapChapterRowData
import kotlin.math.max
import kotlin.time.Clock.System.now
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.datetime.LocalDate

@Composable
fun ChapterOverview(
    viewModel: ChapterViewModel = hiltViewModel(),
    showSnackbar: suspend (SnackbarData) -> Unit,
    onBackClick: () -> Unit,
    navigateToChapter: (ChapterId) -> Unit,
    onBackgroundColorChanged: (Color) -> Unit,
) {
    LaunchedEffect(viewModel, showSnackbar) {
        viewModel.snackbarFlow.collect {
            showSnackbar(it)
        }
    }
    LaunchedEffect(viewModel) {
        viewModel.dominantColor.collect {
            it?.let { p1 -> onBackgroundColorChanged(p1) }
        }
    }
    val state by viewModel.state.collectAsStateWithLifecycle()
    val refreshingState = viewModel.refreshingState.collectAsStateWithLifecycle()
    ChapterOverview(
        state = state,
        onBackClick = onBackClick,
        refreshing = refreshingState,
        startRefresh = dropUnlessStarted { viewModel.onClickRefresh() },
        toggleFavorite = dropUnlessStarted { viewModel.toggleFavorite() },
        navigateToChapter = navigateToChapter,
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
fun ChapterOverview(
    state: ChapterScreenState,
    onBackClick: () -> Unit,
    refreshing: State<Boolean>,
    startRefresh: () -> Unit,
    toggleFavorite: () -> Unit,
    navigateToChapter: (ChapterId) -> Unit,
) {
    val lazyListState = rememberLazyListState()
    val manuallyScrolled = rememberManualScrollState(lazyListState)
    val topAppBarState = rememberTopAppBarState()

    val readyState = state.ifReady()
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
                            Icon(Icons.AutoMirrored.Rounded.ArrowBack, "Back")
                        }
                    },
                    title = { Text(readyState?.title ?: "Manga") },
                    actions = {
                        IconButton(onClick = toggleFavorite) {
                            val isFavorite = readyState?.isFavorite == true
                            Icon(
                                imageVector = when {
                                    isFavorite -> Icons.Outlined.Favorite
                                    else -> Icons.Outlined.FavoriteBorder
                                },
                                contentDescription = if (isFavorite) "Unfavorite" else "Favorite",
                            )
                        }
                    },
                )
            }
        },
        topBarOffset = { topAppBarState.appBarOffset.floatValue.toInt() },
    ) { scaffoldPadding ->
        when (state) {
            is ChapterScreenState.Loading,
            is ChapterScreenState.Error,
            -> LoadingSpinner(scaffoldPadding)

            is ChapterScreenState.Ready -> {
                val pullToRefreshState = rememberPullToRefreshState()
                PullToRefreshBox(
                    isRefreshing = refreshing.value,
                    onRefresh = startRefresh,
                    modifier = Modifier.fillMaxSize(),
                    state = pullToRefreshState,
                ) {
                    StickyTopEffect(
                        items = state.chapters,
                        listState = lazyListState,
                        manuallyScrolled = manuallyScrolled,
                    )
                    ChaptersList(
                        lazyListState = lazyListState,
                        chapters = state.chapters,
                        modifier = Modifier
                            .fillMaxSize()
                            .nestedScroll(topAppBarState.nestedScrollConnection)
                            .scrollableFade(
                                canScrollBackward = { lazyListState.canScrollBackward },
                                canScrollForward = { lazyListState.canScrollForward },
                            ),
                        contentPadding = scaffoldPadding,
                        navigateToChapter = navigateToChapter,
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
    navigateToChapter: (ChapterId) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    lazyListState: LazyListState = rememberLazyListState(),
) {
    val largeCornerSize = MaterialTheme.shapes.medium.topEnd
    val smallCornerSize = CornerSize(0f)
    val floatAnimationSpec = MaterialTheme.motionScheme.defaultSpatialSpec<Float>()
    val intOffsetAnimateSpec = MaterialTheme.motionScheme.defaultSpatialSpec<IntOffset>()
    LazyColumn(
        modifier = modifier,
        state = lazyListState,
        contentPadding = contentPadding + PaddingValues(start = 8.dp, end = 8.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        section(
            header = null,
            items = chapters,
            smallCornerSize = smallCornerSize,
            largeCornerSize = largeCornerSize,
            key = { item -> item.id.inner },
        ) { item, shape ->
            ChapterRow(
                item = item,
                navigateToChapter = navigateToChapter,
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
        Layout(
            modifier = Modifier
                .defaultMinSize(minHeight = 48.dp)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            content = {
                val typography = MaterialTheme.typography
                NumberDisplay(item)
                Text(
                    text = item.date,
                    style = if (item.isRead) typography.titleMedium else typography.titleMediumEmphasized,
                    textAlign = TextAlign.End,
                )
                item.title?.let {
                    Text(
                        text = it,
                        style = if (item.isRead) typography.bodyMedium else typography.bodyMediumEmphasized,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.End,
                    )
                }
            },
        ) { measurables, constraints ->
            val horizontalPadding = 8.dp.toPx()
            val verticalPadding = 4.dp.toPx()

            val contentConstraints = constraints.copy(minHeight = 0)
            val numberPlaceable = measurables[0].measure(contentConstraints)

            val textConstraints = contentConstraints.copy(
                maxWidth = (constraints.maxWidth - numberPlaceable.width - horizontalPadding).toInt(),
            )
            val textPlaceables = measurables.drop(1).map { it.measure(textConstraints) }
            val textHeight =
                textPlaceables.sumOf { it.measuredHeight } +
                    (textPlaceables.size - 1).coerceAtLeast(0) * verticalPadding

            val height = max(constraints.minHeight, textHeight.toInt())
            layout(constraints.maxWidth, height) {
                numberPlaceable.placeRelative(0, (constraints.minHeight - numberPlaceable.measuredHeight) / 2)

                if (textPlaceables.isNotEmpty()) {
                    var textY = (constraints.minHeight - textHeight).coerceAtLeast(0f) / 2f
                    textPlaceables.forEach {
                        it.placeRelative(constraints.maxWidth - it.width, textY.toInt())
                        textY += it.measuredHeight + verticalPadding
                    }
                }
            }
        }
    }
}

@Composable
private fun NumberDisplay(item: ChapterRowData, modifier: Modifier = Modifier) {
    CompositionLocalProvider(LocalContentColor provides LocalContentColor.current.copy(alpha = 1f)) {
        val padding = with(LocalDensity.current) { 3.sp.toDp() }
        Row(
            modifier = modifier,
            horizontalArrangement = Arrangement.spacedBy(padding),
        ) {
            Text(
                text = item.index.toString(),
                style = MaterialTheme.typography.titleLarge,
                fontFamily = FontFamilies.Coiny,
            )
            item.subIndex?.let {
                Text(
                    text = it.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    fontFamily = FontFamilies.Coiny,
                )
            }
        }
    }
}

@Preview("Light")
@Preview("Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewManga(@PreviewParameter(ChapterOverviewScreenStateProvider::class) state: ChapterScreenState) {
    val refreshing = remember { mutableStateOf(false) }
    MangaReaderTheme {
        ChapterOverview(
            state = state,
            onBackClick = {},
            navigateToChapter = {},
            refreshing = refreshing,
            startRefresh = {},
            toggleFavorite = {},
        )
    }
}

class ChapterOverviewScreenStateProvider : PreviewParameterProvider<ChapterScreenState> {
    override val values
        get() = sequenceOf(
            ChapterScreenState.Loading,
            ChapterScreenState.Ready(
                title = "Heavenly Martial God",
                isFavorite = true,
                chapters = ChapterProvider.values.toImmutableList(),
            ),
        )
}

private object ChapterProvider {
    private val mapChapterRowData = MapChapterRowData()

    val values = sequenceOf(
        mapChapterRowData.invoke(
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
                isRead = true,
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
