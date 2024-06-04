package com.spiderbiggen.manga.presentation.ui.images

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.outlined.BookmarkAdded
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.TopAppBarState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.SubcomposeAsyncImage
import com.spiderbiggen.manga.domain.model.SurroundingChapters
import com.spiderbiggen.manga.domain.model.id.ChapterId
import com.spiderbiggen.manga.presentation.components.ListImagePreloader
import com.spiderbiggen.manga.presentation.theme.MangaReaderTheme
import kotlinx.coroutines.launch

@Composable
fun ImagesOverview(viewModel: ImagesViewModel, onBackClick: () -> Unit, toChapterClicked: (ChapterId) -> Unit) {
    LaunchedEffect(true) {
        viewModel.collect()
    }
    val state by viewModel.state.collectAsStateWithLifecycle()

    MangaReaderTheme {
        ImagesOverview(
            state = state,
            onBackClick = onBackClick,
            toChapterClicked = toChapterClicked,
            toggleFavorite = viewModel::toggleFavorite,
            setRead = viewModel::updateReadState,
            setReadUpToHere = viewModel::setReadUpToHere,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ImagesOverview(
    state: ImagesScreenState,
    onBackClick: () -> Unit,
    toChapterClicked: (ChapterId) -> Unit,
    toggleFavorite: () -> Unit,
    setRead: () -> Unit,
    setReadUpToHere: () -> Unit,
) {
    val topAppBarState: TopAppBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(topAppBarState)

    val ready = state.ifReady()
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, "Back")
                    }
                },
                title = { Text(ready?.title.orEmpty()) },
                scrollBehavior = scrollBehavior,
            )
        },
        bottomBar = {
            Surface(Modifier.fillMaxWidth()) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                ) {
                    IconButton(onClick = toggleFavorite) {
                        Icon(
                            imageVector = when (ready?.isFavorite) {
                                true -> Icons.Outlined.Favorite
                                else -> Icons.Outlined.FavoriteBorder
                            },
                            contentDescription = "Favorite",
                        )
                    }
                    IconButton(onClick = setReadUpToHere) {
                        Icon(
                            imageVector = when (ready?.isRead) {
                                true -> Icons.Outlined.BookmarkAdded
                                else -> Icons.Outlined.BookmarkBorder
                            },
                            contentDescription = "Read",
                        )
                    }

                    val previousChapterId = ready?.surrounding?.previous
                    val nextChapterId = ready?.surrounding?.next
                    IconButton(
                        onClick = { previousChapterId?.let { toChapterClicked(it) } },
                        enabled = previousChapterId != null,
                    ) {
                        Icon(Icons.AutoMirrored.Rounded.KeyboardArrowLeft, null)
                    }
                    IconButton(
                        onClick = { nextChapterId?.let { toChapterClicked(it) } },
                        enabled = nextChapterId != null,
                    ) {
                        Icon(Icons.AutoMirrored.Rounded.KeyboardArrowRight, null)
                    }
                }
            }
        },
        contentWindowInsets = WindowInsets.navigationBars,
    ) { padding ->
        when (state) {
            is ImagesScreenState.Loading -> {
                Box(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }

            is ImagesScreenState.Ready -> ReadyImagesOverview(
                state = state,
                scrollBehavior = scrollBehavior,
                padding = padding,
                setRead = setRead,
            )

            is ImagesScreenState.Error -> Text(state.errorMessage)
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun ReadyImagesOverview(
    state: ImagesScreenState.Ready,
    scrollBehavior: TopAppBarScrollBehavior,
    padding: PaddingValues,
    setRead: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState()
    val images by remember { derivedStateOf { state.images } }
    val interactionSource = remember { MutableInteractionSource() }
    ListImagePreloader(
        items = images,
        lazyListState = lazyListState,
    )
    LazyColumn(
        Modifier
            .consumeWindowInsets(padding)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
            ) {
                scope.launch {
                    lazyListState.scrollBy(-(scrollBehavior.state.heightOffset))
                    scrollBehavior.state.heightOffset = 0f
                }
            }
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        contentPadding = padding,
        state = lazyListState,
    ) {
        items(images, key = { it }) {
            ListImage(it, Modifier.fillParentMaxWidth())
        }
        item(key = "setReadEffect", contentType = "Effect") {
            LaunchedEffect(true) { setRead() }
        }
    }
}

private val boxModifier = Modifier
    .fillMaxWidth()
    .aspectRatio(1f)

@Composable
private fun ListImage(model: String, modifier: Modifier = Modifier) {
    SubcomposeAsyncImage(
        model = model,
        contentDescription = null,
        modifier = modifier,
        contentScale = ContentScale.FillWidth,
        loading = {
            Box(
                boxModifier,
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
        },
        error = {
            Box(
                boxModifier.background(MaterialTheme.colorScheme.error),
            )
        },
    )
}

@Preview
@Composable
fun PreviewImagesOverview() {
    MangaReaderTheme {
        ImagesOverview(
            state = ImagesScreenState.Ready(
                title = "Heavenly Martial God",
                isFavorite = true,
                isRead = false,
                surrounding = SurroundingChapters(
                    previous = null,
                    next = null,
                ),
                images = listOf(
                    "https://manga.spiderbiggen.com/api/v1/mangas/8430c4857ec14234811b7508d83a50ab/image"
                )
            ),
            onBackClick = {},
            toChapterClicked = {},
            toggleFavorite = {},
            setRead = {},
            setReadUpToHere = {},
        )
    }
}
