package com.spiderbiggen.manhwa.presentation.ui.chapter.overview

import android.content.res.Configuration
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.material3.surfaceColorAtElevation
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
import com.spiderbiggen.manhwa.domain.model.Chapter
import com.spiderbiggen.manhwa.domain.model.Manhwa
import com.spiderbiggen.manhwa.presentation.theme.ManhwaReaderTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import java.net.URL

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChapterOverview(
    onBackClick: () -> Unit,
    navigateToChapter: (String) -> Unit,
    viewModel: ChapterViewModel = viewModel()
) {
    LaunchedEffect(null) {
        viewModel.collect()
    }
    val scope = rememberCoroutineScope()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val lazyListState = rememberLazyListState()
    val topAppBarState = rememberTopAppBarState()
    ChapterOverview(
        onBackClick = onBackClick,
        navigateToChapter = navigateToChapter,
        refreshing = viewModel.refreshing.value,
        onRefreshClicked = { scope.launch { viewModel.onClickRefresh() } },
        toggleFavorite = { scope.launch { viewModel.toggleFavorite() } },
        state = state,
        lazyListState = lazyListState,
        topAppBarState = topAppBarState,
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ChapterOverview(
    onBackClick: () -> Unit,
    navigateToChapter: (String) -> Unit,
    refreshing: Boolean,
    onRefreshClicked: () -> Unit,
    toggleFavorite: () -> Unit,
    state: ChapterScreenState,
    scope: CoroutineScope = rememberCoroutineScope(),
    lazyListState: LazyListState = rememberLazyListState(),
    topAppBarState: TopAppBarState = rememberTopAppBarState(),
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(topAppBarState)

    val ready = state.ifReady()
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Rounded.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    scrolledContainerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(6.dp)
                ),
                title = { Text((state as? ChapterScreenState.Ready)?.manhwa?.title ?: "Manhwa") },
                scrollBehavior = scrollBehavior,
                actions = {
                    IconButton(
                        onClick = { scope.launch { toggleFavorite() } }
                    ) {
                        Icon(
                            imageVector = if (ready?.isFavorite == true) Icons.Outlined.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Favorite"
                        )
                    }
                    val rotation = if (refreshing) {
                        val infiniteTransition = rememberInfiniteTransition(label = "Refresh")
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
        }
    ) { padding ->
        when (state) {
            ChapterScreenState.Loading,
            is ChapterScreenState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            is ChapterScreenState.Ready -> {
                LazyColumn(
                    Modifier
                        .padding(padding)
                        .nestedScroll(scrollBehavior.nestedScrollConnection),
                    state = lazyListState,
                ) {
                    itemsIndexed(
                        state.chapters,
                        key = { _, item -> item.chapter.id }) { index, item ->
                        ChapterRow(
                            index > 0,
                            item.chapter,
                            item.isRead,
                            navigateToChapter,
                            Modifier.animateItemPlacement()
                        )
                    }
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
        modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 48.dp)
            .clickable { navigateToChapter(item.id) },
        tonalElevation = if (isRead) 0.dp else 3.dp,
    ) {
        Column(Modifier.fillMaxWidth()) {
            if (showDivider) Divider()
            Row(
                Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(
                    Modifier
                        .weight(1f),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically)
                ) {
                    Text(
                        title,
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
fun PreviewManhwa(@PreviewParameter(ChapterOverviewScreenStateProvider::class) state: ChapterScreenState) {
    ManhwaReaderTheme {
        ChapterOverview(
            onBackClick = {},
            navigateToChapter = {},
            refreshing = false,
            onRefreshClicked = {},
            toggleFavorite = {}, state = state
        )
    }
}

class ChapterOverviewScreenStateProvider : PreviewParameterProvider<ChapterScreenState> {
    override val values
        get() = sequenceOf(
            ChapterScreenState.Loading,
            ChapterScreenState.Error("An error occurred"),
            ChapterScreenState.Ready(
                manhwa = ManhwaProvider.value,
                isFavorite = false,
                chapters = ChapterProvider.values.toList()
            ),
            ChapterScreenState.Ready(
                manhwa = ManhwaProvider.value,
                isFavorite = true,
                chapters = ChapterProvider.values.toList()
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

object ManhwaProvider {
    val value = Manhwa(
        source = "Asura",
        id = "7df204a8-2d37-42d1-a2e0-e795ae618388",
        title = "Heavenly Martial God",
        coverImage = URL("https://www.asurascans.com/wp-content/uploads/2021/09/martialgod.jpg"),
        description = "“Who’s this male prostitute-looking kid?” I am the Matchless Ha Hoo Young, the greatest martial artist reigning over all the lands!",
        status = "Ongoing",
        updatedAt = Clock.System.now()
    )
}
