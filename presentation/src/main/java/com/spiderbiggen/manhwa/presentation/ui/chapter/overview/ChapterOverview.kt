package com.spiderbiggen.manhwa.presentation.ui.chapter.overview

import android.content.res.Configuration
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.spiderbiggen.manhwa.domain.model.Chapter
import com.spiderbiggen.manhwa.domain.model.Manga
import com.spiderbiggen.manhwa.presentation.theme.MangaReaderTheme
import com.spiderbiggen.manhwa.presentation.theme.Purple80
import java.net.URL
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate

@Composable
fun ChapterOverview(
    onColorChanged: (Color) -> Unit,
    onBackClick: () -> Unit,
    navigateToChapter: (String) -> Unit,
    viewModel: ChapterViewModel = viewModel(),
    refreshing: State<Boolean> = remember { mutableStateOf(false) },
) {
    LaunchedEffect(null) {
        viewModel.collect()
    }
    val state by viewModel.state.collectAsStateWithLifecycle()
    val lazyListState = rememberLazyListState()
    ChapterOverview(
        onColorChanged = onColorChanged,
        onBackClick = onBackClick,
        navigateToChapter = navigateToChapter,
        refreshing = refreshing.value,
        startRefresh = viewModel::onClickRefresh,
        toggleFavorite = viewModel::toggleFavorite,
        state = state,
        lazyListState = lazyListState,
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ChapterOverview(
    onColorChanged: (Color) -> Unit,
    onBackClick: () -> Unit,
    navigateToChapter: (String) -> Unit,
    refreshing: Boolean,
    startRefresh: () -> Unit,
    toggleFavorite: () -> Unit,
    state: ChapterScreenState,
    lazyListState: LazyListState = rememberLazyListState(),
) {
    val topAppBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val pullToRefreshState = rememberPullToRefreshState()

    LaunchedEffect(refreshing) {
        if (refreshing) {
            pullToRefreshState.startRefresh()
        } else {
            pullToRefreshState.endRefresh()
        }
    }

    if (pullToRefreshState.isRefreshing) {
        LaunchedEffect(true) {
            startRefresh()
        }
    }
    val scaleFraction = if (pullToRefreshState.isRefreshing) {
        1f
    } else {
        LinearOutSlowInEasing.transform(pullToRefreshState.progress).coerceIn(0f, 1f)
    }

    val dominantColor = state.ifReady()?.manga?.dominantColor
    LaunchedEffect(dominantColor) {
        dominantColor?.let { onColorChanged(Color(dominantColor)) }
    }
    Scaffold(
        modifier = Modifier
            .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection)
            .nestedScroll(pullToRefreshState.nestedScrollConnection),
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, "Back")
                    }
                },
                scrollBehavior = topAppBarScrollBehavior,
                title = { Text((state as? ChapterScreenState.Ready)?.manga?.title ?: "Manga") },
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
        Box(
            Modifier
                .padding(padding)
                .fillMaxSize(),
            contentAlignment = Alignment.TopCenter,
        ) {
            when (state) {
                ChapterScreenState.Loading,
                is ChapterScreenState.Error,
                -> CircularProgressIndicator(
                    Modifier.align(
                        Alignment.Center,
                    ),
                )

                is ChapterScreenState.Ready -> {
                    LazyColumn(state = lazyListState) {
                        itemsIndexed(
                            items = state.chapters,
                            key = { index, item -> if (index == 0) "first" else item.chapter.id },
                        ) { index, item ->
                            ChapterRow(
                                showDivider = index > 0,
                                item = item.chapter,
                                isRead = item.isRead,
                                navigateToChapter = navigateToChapter,
                                modifier = Modifier.animateItemPlacement(),
                            )
                        }
                    }
                    PullToRefreshContainer(
                        state = pullToRefreshState,
                        modifier = Modifier.graphicsLayer(
                            scaleX = scaleFraction,
                            scaleY = scaleFraction,
                        ),
                    )
                }
            }
        }
    }
}

@Composable
private fun ChapterRow(
    showDivider: Boolean,
    item: Chapter,
    isRead: Boolean,
    navigateToChapter: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val title by remember(item) {
        derivedStateOf {
            StringBuilder().apply {
                append(item.number)
                item.decimal?.let {
                    append('.').append(it)
                }
                item.title?.let {
                    if (it[0].isLetterOrDigit()) append(" - ")
                    append(it)
                }
            }.toString()
        }
    }
    Surface(
        onClick = { navigateToChapter(item.id) },
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
                        if (isRead) it.copy(alpha = 0.7f) else it
                    }
                    CompositionLocalProvider(LocalContentColor provides contentColor) {
                        Text(
                            title,
                            fontWeight = if (!isRead) FontWeight.Bold else null,
                            style = MaterialTheme.typography.bodyLarge,
                        )
                        Text(
                            item.date.toString(),
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
fun PreviewManga(
    @PreviewParameter(ChapterOverviewScreenStateProvider::class) state: ChapterScreenState,
) {
    var seedColor by remember { mutableStateOf(Purple80) }
    MangaReaderTheme(seedColor) {
        ChapterOverview(
            onColorChanged = { seedColor = it },
            onBackClick = {},
            navigateToChapter = {},
            refreshing = false,
            startRefresh = {},
            toggleFavorite = {},
            state = state,
        )
    }
}

class ChapterOverviewScreenStateProvider : PreviewParameterProvider<ChapterScreenState> {
    override val values
        get() = sequenceOf(
            ChapterScreenState.Loading,
            ChapterScreenState.Ready(
                manga = MangaProvider.value,
                isFavorite = true,
                chapters = ChapterProvider.values.toList(),
            ),
        )
}

object ChapterProvider {
    val values = sequenceOf(
        ChapterRowData(
            chapter = Chapter(
                id = "000000",
                number = 30,
                decimal = null,
                title = null,
                date = LocalDate(2023, 4, 16),
                updatedAt = Instant.DISTANT_PAST,
            ),
            isRead = false,
        ),
        ChapterRowData(
            chapter = Chapter(
                id = "000001",
                number = 29,
                decimal = 5,
                title = null,
                date = LocalDate(2023, 4, 12),
                updatedAt = Instant.DISTANT_PAST,
            ),
            isRead = true,
        ),
        ChapterRowData(
            chapter = Chapter(
                id = "000002",
                number = 39,
                decimal = null,
                title = null,
                date = LocalDate(2023, 3, 15),
                updatedAt = Instant.DISTANT_PAST,
            ),
            isRead = true,
        ),
        ChapterRowData(
            chapter = Chapter(
                id = "000003",
                number = 30,
                decimal = null,
                title = "Long title to make the title take two lines at least",
                date = LocalDate(2023, 2, 28),
                updatedAt = Instant.DISTANT_PAST,
            ),
            isRead = true,
        ),
    )
}

object MangaProvider {
    val value = Manga(
        source = "Asura",
        id = "712dd47d-6465-4433-8484-357604d6cf80",
        title = "Heavenly Martial God",
        coverImage = URL("https://www.asurascans.com/wp-content/uploads/2021/09/martialgod.jpg"),
        dominantColor = 0xFF1818,
        description = """
            “Who’s this male prostitute-looking kid?” I am the Matchless Ha Hoo Young,
             the greatest martial artist reigning over all the lands!
        """.trimIndent(),
        status = "Ongoing",
        updatedAt = Clock.System.now(),
    )
}
