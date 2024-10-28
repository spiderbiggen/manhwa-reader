package com.spiderbiggen.manga.presentation.ui.chapter.read

import android.content.res.Configuration
import androidx.compose.animation.core.animateIntOffsetAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
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
import androidx.compose.material3.BottomAppBarDefaults
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.dropUnlessStarted
import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.annotation.ExperimentalCoilApi
import coil3.asImage
import coil3.compose.AsyncImagePainter
import coil3.compose.AsyncImagePreviewHandler
import coil3.compose.LocalAsyncImagePreviewHandler
import coil3.compose.LocalPlatformContext
import coil3.compose.asPainter
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ErrorResult
import coil3.request.SuccessResult
import com.spiderbiggen.manga.domain.model.SurroundingChapters
import com.spiderbiggen.manga.domain.model.id.ChapterId
import com.spiderbiggen.manga.presentation.R
import com.spiderbiggen.manga.presentation.theme.MangaReaderTheme
import kotlinx.collections.immutable.persistentListOf

@Composable
fun ReadChapterScreen(
    viewModel: ImagesViewModel = hiltViewModel(),
    imageLoader: ImageLoader,
    onBackClick: () -> Unit,
    toChapterClicked: (ChapterId) -> Unit,
) {
    LaunchedEffect(viewModel) {
        viewModel.collect()
    }
    val state by viewModel.state.collectAsStateWithLifecycle()

    MangaReaderTheme {
        ReadChapterScreen(
            state = state,
            imageLoader = imageLoader,
            onBackClick = onBackClick,
            toChapterClicked = toChapterClicked,
            toggleFavorite = dropUnlessStarted { viewModel.toggleFavorite() },
            setRead = dropUnlessStarted { viewModel.updateReadState() },
            setReadUpToHere = dropUnlessStarted { viewModel.setReadUpToHere() },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReadChapterScreen(
    state: ImagesScreenState,
    imageLoader: ImageLoader = SingletonImageLoader.get(LocalContext.current),
    onBackClick: () -> Unit = {},
    toChapterClicked: (ChapterId) -> Unit = {},
    toggleFavorite: () -> Unit = {},
    setRead: () -> Unit = {},
    setReadUpToHere: () -> Unit = {},
) {
    val density = LocalDensity.current
    val skipToExpandedBars = with(density) {
        2.dp.toPx()
    }
    val maxTopOffSet = with(density) {
        TopAppBarDefaults.windowInsets.getTop(density) + TopAppBarDefaults.TopAppBarExpandedHeight.toPx().toInt()
    }
    val maxBottomOffSet = with(density) {
        BottomAppBarDefaults.windowInsets.getBottom(density) + 56.dp.toPx().toInt()
    }

    val lazyListState = rememberLazyListState()

    var topAppBarOffsetPx by remember { mutableIntStateOf(0) }
    var bottomBarOffsetPx by remember { mutableIntStateOf(0) }

    val animatedTopBarIntOffset by animateIntOffsetAsState(IntOffset(0, topAppBarOffsetPx))
    val animatedBottomBarIntOffset by animateIntOffsetAsState(IntOffset(0, bottomBarOffsetPx))

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource): Offset {
                val delta = consumed.y
                if (delta > skipToExpandedBars) {
                    topAppBarOffsetPx = 0
                    bottomBarOffsetPx = 0
                } else {
                    topAppBarOffsetPx = (topAppBarOffsetPx + delta).toInt().coerceIn(-maxTopOffSet, 0)
                    val maximumValue = maxBottomOffSet - getBottomPadding(lazyListState, maxBottomOffSet)
                    bottomBarOffsetPx = (bottomBarOffsetPx - delta).toInt().coerceIn(0, maximumValue.coerceAtLeast(0))
                }
                return Offset.Zero
            }
        }
    }

    val ready = state.ifReady()
    Scaffold(
        contentWindowInsets = WindowInsets.navigationBars,
        topBar = {
            Box(
                Modifier
                    .offset { animatedTopBarIntOffset }
                    .background(MaterialTheme.colorScheme.surface)
                    .windowInsetsPadding(TopAppBarDefaults.windowInsets),
            ) {
                TopAppBar(
                    navigationIcon = {
                        IconButton(onClick = dropUnlessStarted(block = onBackClick)) {
                            Icon(Icons.AutoMirrored.Rounded.ArrowBack, "Back")
                        }
                    },
                    title = { Text(ready?.title.orEmpty()) },
                )
            }
        },
        bottomBar = {
            Surface(
                Modifier
                    .offset { animatedBottomBarIntOffset }
                    .fillMaxWidth(),
            ) {
                Row(Modifier.padding(horizontal = 16.dp)) {
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
                        onClick = dropUnlessStarted { previousChapterId?.let { toChapterClicked(it) } },
                        enabled = previousChapterId != null,
                    ) {
                        Icon(Icons.AutoMirrored.Rounded.KeyboardArrowLeft, null)
                    }
                    IconButton(
                        onClick = dropUnlessStarted { nextChapterId?.let { toChapterClicked(it) } },
                        enabled = nextChapterId != null,
                    ) {
                        Icon(Icons.AutoMirrored.Rounded.KeyboardArrowRight, null)
                    }
                }
            }
        },
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
                imageLoader = imageLoader,
                lazyListState = lazyListState,
                nestedScrollConnection = nestedScrollConnection,
                padding = padding,
                setRead = setRead,
            )

            is ImagesScreenState.Error -> Text(state.errorMessage)
        }
    }
}

private fun getBottomPadding(lazyListState: LazyListState, maxBottomOffSet: Int): Int {
    val info = lazyListState.layoutInfo
    val count = info.totalItemsCount
    val lastVisibleItem = info.visibleItemsInfo.lastOrNull() ?: return maxBottomOffSet
    if (lastVisibleItem.index + 2 < count) return 0
    val consumedSize = lastVisibleItem.offset + lastVisibleItem.size + info.afterContentPadding
    val bottomOverflow = (consumedSize - info.viewportEndOffset)
    val bottomPadding = (maxBottomOffSet - bottomOverflow).coerceAtLeast(0)
    return bottomPadding
}

@Composable
private fun ReadyImagesOverview(
    state: ImagesScreenState.Ready,
    imageLoader: ImageLoader,
    lazyListState: LazyListState,
    nestedScrollConnection: NestedScrollConnection,
    padding: PaddingValues,
    setRead: () -> Unit,
) {
    val images by remember { derivedStateOf { state.images } }
    LazyColumn(
        Modifier.nestedScroll(nestedScrollConnection),
        contentPadding = padding,
        state = lazyListState,
    ) {
        items(images, key = { it }) {
            ListImage(it, imageLoader, Modifier.fillParentMaxWidth())
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
private fun ListImage(model: String, imageLoader: ImageLoader, modifier: Modifier = Modifier) {
    val asyncPainter = rememberAsyncImagePainter(
        model = model,
        imageLoader = imageLoader,
    )

    val painterState = asyncPainter.state.collectAsState()
    when (val state = painterState.value) {
        is AsyncImagePainter.State.Success -> {
            Image(
                state.painter,
                null,
                modifier = modifier,
                contentScale = ContentScale.FillWidth,
            )
        }

        is AsyncImagePainter.State.Error -> {
            Box(boxModifier.background(MaterialTheme.colorScheme.error))
        }

        else -> {
            Box(boxModifier, contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
    }
}

@OptIn(ExperimentalCoilApi::class)
@Preview("Light")
@Preview("Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewImagesOverview() {
    val context = LocalPlatformContext.current
    val previewHandler = AsyncImagePreviewHandler { _, request ->
        when (request.data.toString()) {
            "1" -> {
                val image = context.resources.getDrawable(R.mipmap.preview_cover_placeholder, null).asImage()
                AsyncImagePainter.State.Success(image.asPainter(context), SuccessResult(image, request))
            }

            "2" -> AsyncImagePainter.State.Error(null, ErrorResult(null, request, Throwable()))
            "3" -> AsyncImagePainter.State.Loading(null)

            else -> AsyncImagePainter.State.Empty
        }
    }

    CompositionLocalProvider(LocalAsyncImagePreviewHandler provides previewHandler) {
        MangaReaderTheme {
            ReadChapterScreen(
                state = ImagesScreenState.Ready(
                    title = "Heavenly Martial God",
                    isFavorite = true,
                    isRead = false,
                    surrounding = SurroundingChapters(
                        previous = null,
                        next = null,
                    ),
                    images = persistentListOf(
                        "1",
                        "2",
                        "3",
                        "4",
                    ),
                ),
            )
        }
    }
}
