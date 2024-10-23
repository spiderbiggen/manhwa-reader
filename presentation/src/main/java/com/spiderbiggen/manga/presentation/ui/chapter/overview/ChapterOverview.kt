@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.spiderbiggen.manga.presentation.ui.chapter.overview

import android.content.res.Configuration
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleStartEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.dropUnlessResumed
import androidx.lifecycle.coroutineScope
import com.spiderbiggen.manga.domain.model.Chapter
import com.spiderbiggen.manga.domain.model.id.ChapterId
import com.spiderbiggen.manga.presentation.components.LoadingSpinner
import com.spiderbiggen.manga.presentation.components.ReadableCard
import com.spiderbiggen.manga.presentation.components.StickyTopEffect
import com.spiderbiggen.manga.presentation.components.rememberManualScrollState
import com.spiderbiggen.manga.presentation.extensions.plus
import com.spiderbiggen.manga.presentation.theme.MangaReaderTheme
import com.spiderbiggen.manga.presentation.theme.Purple80
import com.spiderbiggen.manga.presentation.ui.chapter.overview.model.ChapterRowData
import com.spiderbiggen.manga.presentation.ui.chapter.overview.usecase.MapChapterRowData
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock.System.now
import kotlinx.datetime.LocalDate

@Composable
fun ChapterOverview(
    viewModel: ChapterViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    navigateToChapter: (ChapterId) -> Unit,
) {
    LifecycleStartEffect(viewModel) {
        val job = lifecycle.coroutineScope.launch {
            viewModel.collect()
        }
        onStopOrDispose {
            job.cancel()
        }
    }
    val state by viewModel.state.collectAsStateWithLifecycle()
    val refreshingState = viewModel.refreshingState.collectAsState()
    ChapterOverview(
        state = state,
        onBackClick = onBackClick,
        refreshing = refreshingState,
        startRefresh = dropUnlessResumed { viewModel.onClickRefresh() },
        toggleFavorite = dropUnlessResumed { viewModel.toggleFavorite() },
        navigateToChapter = navigateToChapter,
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
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
    val topAppBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    MangaReaderTheme(state.ifReady()?.dominantColor ?: Purple80) {
        Scaffold(
            topBar = {
                TopAppBar(
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.AutoMirrored.Rounded.ArrowBack, "Back")
                        }
                    },
                    scrollBehavior = topAppBarScrollBehavior,
                    title = { Text((state as? ChapterScreenState.Ready)?.title ?: "Manga") },
                    actions = {
                        IconButton(onClick = toggleFavorite) {
                            Icon(
                                imageVector = when (state.ifReady()?.isFavorite) {
                                    true -> Icons.Outlined.Favorite
                                    else -> Icons.Outlined.FavoriteBorder
                                },
                                contentDescription = "Favorite",
                            )
                        }
                    },
                )
            },
        ) { padding ->
            when (state) {
                is ChapterScreenState.Loading,
                is ChapterScreenState.Error,
                -> LoadingSpinner(padding)

                is ChapterScreenState.Ready -> {
                    PullToRefreshBox(
                        isRefreshing = refreshing.value,
                        onRefresh = startRefresh,
                        modifier = Modifier.fillMaxSize(),
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
                                .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
                            contentPadding = padding,
                            navigateToChapter = navigateToChapter,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ChaptersList(
    chapters: ImmutableList<ChapterRowData>,
    navigateToChapter: (ChapterId) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    lazyListState: LazyListState = rememberLazyListState(),
) {
    LazyColumn(
        modifier = modifier,
        state = lazyListState,
        contentPadding = contentPadding + PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(
            items = chapters,
            key = { item -> item.id.inner },
        ) { item ->
            ChapterRow(
                item = item,
                navigateToChapter = navigateToChapter,
                modifier = Modifier.animateItem(),
            )
        }
    }
}

@Composable
private fun ChapterRow(
    item: ChapterRowData,
    navigateToChapter: (ChapterId) -> Unit,
    modifier: Modifier = Modifier
) {
    ReadableCard(
        isRead = item.isRead,
        onClick = dropUnlessResumed { navigateToChapter(item.id) },
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 48.dp),
    ) {
        Row(
            Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            NumberDisplay(item, Modifier.align(Alignment.Top))
            Column(verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically)) {
                val color by animateColorAsState(
                    LocalContentColor.current.let {
                        if (item.isRead) it.copy(alpha = 0.7f) else it
                    },
                    label = "content color",
                )
                CompositionLocalProvider(LocalContentColor provides color) {
                    Text(
                        item.date,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    item.title?.let {
                        Text(
                            it,
                            fontWeight = if (!item.isRead) FontWeight.Bold else null,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NumberDisplay(item: ChapterRowData, modifier: Modifier) {
    Box(
        modifier
            .background(
                MaterialTheme.colorScheme.primaryContainer,
                shape = MaterialTheme.shapes.medium
            )
            .size(56.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = item.number,
            modifier = Modifier.padding(4.dp),
            style = MaterialTheme.typography.titleLarge.copy(color = MaterialTheme.colorScheme.onPrimaryContainer),
            textAlign = TextAlign.Center,
        )
    }
}

@Preview("Light")
@Preview("Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewManga(@PreviewParameter(ChapterOverviewScreenStateProvider::class) state: ChapterScreenState) {
    val refreshing = remember { mutableStateOf(false) }
    MangaReaderTheme(state.ifReady()?.dominantColor ?: Purple80) {
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
                dominantColor = Color(0xFFFF1818),
                isFavorite = true,
                chapters = ChapterProvider.values.toImmutableList(),
            ),
        )
}

private object ChapterProvider {
    private val mapChapterRowData = MapChapterRowData()

    val values = sequenceOf(
        mapChapterRowData(
            chapter = Chapter(
                id = ChapterId("000000"),
                number = 30.0,
                title = null,
                date = LocalDate.parse("2023-04-16"),
                updatedAt = now(),
            ),
            isRead = false,
        ),
        mapChapterRowData(
            chapter = Chapter(
                id = ChapterId("000001"),
                number = 29.5,
                title = null,
                date = LocalDate.parse("2023-04-12"),
                updatedAt = now(),
            ),
            isRead = true,
        ),
        mapChapterRowData(
            chapter = Chapter(
                id = ChapterId("000002"),
                number = 29.0,
                title = null,
                date = LocalDate.parse("2023-03-15"),
                updatedAt = now(),
            ),
            isRead = false,
        ),
        mapChapterRowData(
            chapter = Chapter(
                id = ChapterId("000003"),
                number = 28.0,
                title = "Long title to make the title take two lines at least",
                date = LocalDate.parse("2023-02-28"),
                updatedAt = now(),
            ),
            isRead = false,
        ),
    )
}
