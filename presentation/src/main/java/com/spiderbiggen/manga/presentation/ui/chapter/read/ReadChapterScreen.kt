package com.spiderbiggen.manga.presentation.ui.chapter.read

import android.content.res.Configuration
import androidx.compose.animation.core.animateIntOffsetAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.systemBars
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
import androidx.compose.runtime.IntState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
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
    val lazyListState = rememberLazyListState()

    val topAppBarOffsetPx = rememberSaveable { mutableIntStateOf(0) }
    val bottomBarOffsetPx = rememberSaveable { mutableIntStateOf(0) }

    val nestedScrollConnection = rememberReaderScrollEffect(
        lazyListState = lazyListState,
        topAppBarOffsetPx = topAppBarOffsetPx,
        bottomBarOffsetPx = bottomBarOffsetPx,
    )

    val ready by remember { derivedStateOf { state.ifReady() } }
    Scaffold(
        contentWindowInsets = WindowInsets.navigationBars,
        topBar = {
            ReaderTopAppBar(topAppBarOffsetPx, onBackClick, ready)
        },
        bottomBar = {
            ReaderBottomBar(ready, bottomBarOffsetPx, toChapterClicked, toggleFavorite, setReadUpToHere)
        },
    ) { padding ->
        when (state) {
            is ImagesScreenState.Loading -> Box(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }

            is ImagesScreenState.Ready -> ReadyImagesOverview(
                state = state,
                imageLoader = imageLoader,
                lazyListState = lazyListState,
                nestedScrollConnection = nestedScrollConnection,
                padding = padding,
                onListClicked = {
                    topAppBarOffsetPx.intValue = 0
                    bottomBarOffsetPx.intValue = 0
                },
                setRead = setRead,
            )

            is ImagesScreenState.Error -> Text(state.errorMessage)
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun ReaderTopAppBar(
    topAppBarOffsetPx: MutableIntState,
    onBackClick: () -> Unit,
    ready: ImagesScreenState.Ready?,
) {
    val animatedTopBarIntOffset = animateIntOffsetAsState(IntOffset(0, topAppBarOffsetPx.intValue))
    Box(
        Modifier
            .offset { animatedTopBarIntOffset.value }
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun rememberReaderScrollEffect(
    lazyListState: LazyListState,
    topAppBarOffsetPx: MutableIntState,
    bottomBarOffsetPx: MutableIntState,
) = with(LocalDensity.current) {
    val insets = WindowInsets.systemBars
    remember(this, insets) {
        val maxTopOffSet = insets.getTop(this) + TopAppBarDefaults.TopAppBarExpandedHeight.toPx().toInt()
        val maxBottomOffSet = insets.getBottom(this) + 56.dp.toPx().toInt()
        ReaderScrollEffect(lazyListState, maxTopOffSet, maxBottomOffSet, topAppBarOffsetPx, bottomBarOffsetPx)
    }
}

private class ReaderScrollEffect(
    val lazyListState: LazyListState,
    val maxTopOffSet: Int,
    val maxBottomOffSet: Int,
    val topAppBarOffsetPx: MutableIntState,
    val bottomBarOffsetPx: MutableIntState,
) : NestedScrollConnection {
    override fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource): Offset {
        val delta = consumed.y
        topAppBarOffsetPx.intValue = (topAppBarOffsetPx.intValue + delta).toInt()
            .coerceIn(-maxTopOffSet, 0)

        val maxBottomOffsetValue = maxBottomOffSet - getBottomPadding(lazyListState, maxBottomOffSet)
        bottomBarOffsetPx.intValue = (bottomBarOffsetPx.intValue - delta).toInt()
            .coerceIn(0, maxBottomOffsetValue.coerceAtLeast(0))
        return Offset.Zero
    }

    private fun getBottomPadding(lazyListState: LazyListState, maxBottomOffSet: Int): Int {
        val info = lazyListState.layoutInfo
        val lastVisibleItem = info.visibleItemsInfo.lastOrNull() ?: return maxBottomOffSet

        val count = info.totalItemsCount
        if (lastVisibleItem.index + 2 < count) return 0

        val consumedSize = lastVisibleItem.offset + lastVisibleItem.size + info.afterContentPadding
        val bottomOverflow = (consumedSize - info.viewportEndOffset)
        val bottomPadding = (maxBottomOffSet - bottomOverflow).coerceAtLeast(0)
        return bottomPadding
    }
}

@Composable
private fun ReadyImagesOverview(
    state: ImagesScreenState.Ready,
    imageLoader: ImageLoader,
    lazyListState: LazyListState,
    nestedScrollConnection: NestedScrollConnection,
    padding: PaddingValues,
    onListClicked: () -> Unit,
    setRead: () -> Unit,
) {
    val images by remember { derivedStateOf { state.images } }
    LazyColumn(
        Modifier
            .nestedScroll(nestedScrollConnection)
            .clickable(
                interactionSource = null,
                indication = null,
                onClick = onListClicked,
            ),
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

@Composable
private fun ListImage(model: String, imageLoader: ImageLoader, modifier: Modifier = Modifier) {
    val asyncPainter = rememberAsyncImagePainter(
        model = model,
        imageLoader = imageLoader,
    )

    val painterState = asyncPainter.state.collectAsState()
    when (val state = painterState.value) {
        is AsyncImagePainter.State.Success -> Image(
            painter = state.painter,
            contentDescription = null,
            modifier = modifier,
            contentScale = ContentScale.FillWidth,
        )

        is AsyncImagePainter.State.Error -> Box(
            modifier
                .aspectRatio(1f)
                .background(MaterialTheme.colorScheme.error),
        )

        else -> Box(modifier.aspectRatio(1f), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
}

@Composable
private fun ReaderBottomBar(
    state: ImagesScreenState.Ready?,
    bottomBarOffsetPx: IntState,
    toChapterClicked: (ChapterId) -> Unit = {},
    toggleFavorite: () -> Unit = {},
    setReadUpToHere: () -> Unit = {},
) {
    val animatedBottomBarIntOffset = animateIntOffsetAsState(IntOffset(0, bottomBarOffsetPx.intValue))
    Surface(
        Modifier
            .offset { animatedBottomBarIntOffset.value }
            .fillMaxWidth(),
    ) {
        Row(Modifier.padding(horizontal = 16.dp)) {
            IconButton(onClick = toggleFavorite) {
                Icon(
                    imageVector = when (state?.isFavorite) {
                        true -> Icons.Outlined.Favorite
                        else -> Icons.Outlined.FavoriteBorder
                    },
                    contentDescription = "Favorite",
                )
            }
            IconButton(onClick = setReadUpToHere) {
                Icon(
                    imageVector = when (state?.isRead) {
                        true -> Icons.Outlined.BookmarkAdded
                        else -> Icons.Outlined.BookmarkBorder
                    },
                    contentDescription = "Read",
                )
            }

            val previousChapterId = state?.surrounding?.previous
            IconButton(
                onClick = dropUnlessStarted { previousChapterId?.let { toChapterClicked(it) } },
                enabled = previousChapterId != null,
            ) {
                Icon(Icons.AutoMirrored.Rounded.KeyboardArrowLeft, null)
            }
            val nextChapterId = state?.surrounding?.next
            IconButton(
                onClick = dropUnlessStarted { nextChapterId?.let { toChapterClicked(it) } },
                enabled = nextChapterId != null,
            ) {
                Icon(Icons.AutoMirrored.Rounded.KeyboardArrowRight, null)
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
