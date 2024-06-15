package com.spiderbiggen.manga.presentation.ui.chapter

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleStartEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.dropUnlessResumed
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.spiderbiggen.manga.domain.model.id.ChapterId
import com.spiderbiggen.manga.presentation.components.LoadingSpinner
import com.spiderbiggen.manga.presentation.components.StickyTopEffect
import com.spiderbiggen.manga.presentation.components.UpdatedListButton
import com.spiderbiggen.manga.presentation.components.rememberManualScrollState
import com.spiderbiggen.manga.presentation.theme.MangaReaderTheme
import com.spiderbiggen.manga.presentation.theme.Purple80
import com.spiderbiggen.manga.presentation.ui.chapter.model.ChapterRowData
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch

@Composable
fun ChapterOverview(
    viewModel: ChapterViewModel = viewModel(),
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
    MangaReaderTheme {
        ChapterOverview(
            state = state,
            onBackClick = onBackClick,
            refreshing = refreshingState,
            startRefresh = dropUnlessResumed { viewModel.onClickRefresh() },
            toggleFavorite = dropUnlessResumed { viewModel.toggleFavorite() },
            navigateToChapter = navigateToChapter,
        )
    }
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
    val topAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val lazyListState = rememberLazyListState()
    val manuallyScrolled = rememberManualScrollState(lazyListState)

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
        containerColor = MaterialTheme.colorScheme.surface,
    ) { padding ->
        when (state) {
            is ChapterScreenState.Loading,
            is ChapterScreenState.Error,
            -> LoadingSpinner(padding)

            is ChapterScreenState.Ready -> {
                PullToRefreshBox(
                    isRefreshing = refreshing.value,
                    onRefresh = startRefresh,
                    Modifier
                        .padding(padding)
                        .fillMaxSize(),
                    contentAlignment = Alignment.TopCenter,
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
                        navigateToChapter = navigateToChapter,
                    )
                    UpdatedListButton(
                        collection = state.chapters,
                        key = { it.id.inner },
                        listState = lazyListState,
                        modifier = Modifier.padding(top = 8.dp),
                        manuallyScrolled = manuallyScrolled,
                    )
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
    lazyListState: LazyListState = rememberLazyListState(),
) {
    LazyColumn(
        modifier = modifier,
        state = lazyListState,
    ) {
        itemsIndexed(
            items = chapters,
            key = { _, item -> item.id.inner },
        ) { index, item ->
            ChapterRow(
                showDivider = index > 0,
                item = item,
                navigateToChapter = navigateToChapter,
                modifier = Modifier.animateItem(),
            )
        }
    }
}

@Composable
private fun ChapterRow(
    showDivider: Boolean,
    item: ChapterRowData,
    navigateToChapter: (ChapterId) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        onClick = dropUnlessResumed { navigateToChapter(item.id) },
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 48.dp),
    ) {
        Column(Modifier.fillMaxWidth()) {
            if (showDivider) HorizontalDivider()
            Row(
                Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(
                    Modifier
                        .weight(1f),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
                ) {
                    val contentColor = LocalContentColor.current.let {
                        if (item.isRead) it.copy(alpha = 0.7f) else it
                    }
                    CompositionLocalProvider(LocalContentColor provides contentColor) {
                        Text(
                            item.title,
                            fontWeight = if (!item.isRead) FontWeight.Bold else null,
                            style = MaterialTheme.typography.bodyLarge,
                        )
                        Text(
                            item.date,
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                }
            }
        }
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

object ChapterProvider {
    val values = sequenceOf(
        ChapterRowData(
            id = ChapterId("000000"),
            title = "30",
            date = "2023-04-16",
            isRead = false,
        ),
        ChapterRowData(
            id = ChapterId("000001"),
            title = "29.5",
            date = "2023-04-12",
            isRead = true,
        ),
        ChapterRowData(
            id = ChapterId("000002"),
            title = "39",
            date = "2023-03-15",
            isRead = true,
        ),
        ChapterRowData(
            id = ChapterId("000003"),
            title = "30 - Long title to make the title take two lines at least",
            date = "2023-02-28",
            isRead = true,
        ),
    )
}
