package com.spiderbiggen.manga.presentation.ui.manga.chapter.reader

import android.content.res.Configuration
import androidx.compose.animation.core.AnimationState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateTo
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.BottomAppBarScrollBehavior
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FlexibleBottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.tooling.preview.Wallpapers
import androidx.compose.ui.unit.dp
import androidx.core.content.res.ResourcesCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.dropUnlessStarted
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
import com.spiderbiggen.manga.domain.model.chapter.SurroundingChapters
import com.spiderbiggen.manga.domain.model.id.ChapterId
import com.spiderbiggen.manga.presentation.R
import com.spiderbiggen.manga.presentation.R.drawable.arrow_back
import com.spiderbiggen.manga.presentation.components.FavoriteToggle
import com.spiderbiggen.manga.presentation.components.PreloadImages
import com.spiderbiggen.manga.presentation.components.bottomappbar.lastItemIsVisible
import com.spiderbiggen.manga.presentation.components.bottomappbar.scrollAgainstContentBehavior
import com.spiderbiggen.manga.presentation.components.topappbar.MangaTopAppBar
import com.spiderbiggen.manga.presentation.components.topappbar.scrollWithContentBehavior
import com.spiderbiggen.manga.presentation.theme.MangaReaderTheme
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.launch

@Composable
fun MangaChapterReaderScreen(
    viewModel: MangaChapterReaderViewModel = hiltViewModel(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    onBackClick: () -> Unit,
    onChapterClick: (ChapterId) -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    MangaChapterReaderScreen(
        snackbarHostState = snackbarHostState,
        state = state,
        onBackClick = onBackClick,
        onChapterClick = onChapterClick,
        toggleFavorite = dropUnlessStarted { viewModel.toggleFavorite() },
        setRead = dropUnlessStarted { viewModel.updateReadState() },
        setReadUpToHere = dropUnlessStarted { viewModel.setReadUpToHere() },
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MangaChapterReaderScreen(
    state: MangaChapterReaderScreenState,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    onBackClick: () -> Unit = {},
    onChapterClick: (ChapterId) -> Unit = {},
    toggleFavorite: () -> Unit = {},
    setRead: () -> Unit = {},
    setReadUpToHere: () -> Unit = {},
) {
    val lazyListState = rememberLazyListState()

    val topAppBarScrollBehavior = TopAppBarDefaults.scrollWithContentBehavior(
        canScroll = { lazyListState.canScrollForward || lazyListState.canScrollBackward },
    )
    val bottomAppBarScrollBehavior = BottomAppBarDefaults.scrollAgainstContentBehavior(
        canScroll = { lazyListState.canScrollForward || lazyListState.canScrollBackward },
        lastItemIsVisible = { lastItemIsVisible(lazyListState) },
    )

    Scaffold(
        modifier = Modifier
            .nestedScroll(bottomAppBarScrollBehavior.nestedScrollConnection)
            .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
        topBar = {
            MangaTopAppBar(
                navigationIcon = {
                    IconButton(onClick = dropUnlessStarted(block = onBackClick)) {
                        Icon(painterResource(arrow_back), "Back")
                    }
                },
                title = { Text(text = state.title.orEmpty()) },
                scrollBehavior = topAppBarScrollBehavior,
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (state is MangaChapterReaderScreenState.Ready) {
                ReaderBottomBar(
                    screenState = state,
                    toChapterClicked = onChapterClick,
                    toggleFavorite = toggleFavorite,
                    setReadUpToHere = setReadUpToHere,
                    scrollBehavior = bottomAppBarScrollBehavior,
                )
            }
        },
    ) { contentPadding ->
        when (state) {
            is MangaChapterReaderScreenState.Loading -> Box(
                modifier = Modifier
                    .padding(contentPadding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                LoadingIndicator()
            }

            is MangaChapterReaderScreenState.Ready -> {
                val coroutineScope = rememberCoroutineScope()
                val floatAnimationSpec = MaterialTheme.motionScheme.slowSpatialSpec<Float>()
                ReadyImagesOverview(
                    modifier = Modifier.fillMaxSize(),
                    state = state,
                    padding = contentPadding,
                    lazyListState = lazyListState,
                    onListClicked = clicked@{
                        val initialTopOffset = topAppBarScrollBehavior.state.heightOffset
                        if (initialTopOffset == 0f) return@clicked
                        coroutineScope.launch {
                            var prevOffset = initialTopOffset
                            AnimationState(initialTopOffset).animateTo(0f, floatAnimationSpec) {
                                lazyListState.dispatchRawDelta(value - prevOffset)
                                topAppBarScrollBehavior.nestedScrollConnection.onPostScroll(
                                    consumed = Offset(0f, value - prevOffset),
                                    available = Offset.Zero,
                                    source = NestedScrollSource.SideEffect,
                                )
                                prevOffset = value
                            }
                        }
                        coroutineScope.launch {
                            val initialBottomOffset = bottomAppBarScrollBehavior.state.heightOffset
                            AnimationState(initialBottomOffset).animateTo(0f, floatAnimationSpec) {
                                bottomAppBarScrollBehavior.state.heightOffset = value
                            }
                        }
                    },
                    setRead = setRead,
                )
            }

            is MangaChapterReaderScreenState.Error -> Text(state.errorMessage)
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun ReadyImagesOverview(
    state: MangaChapterReaderScreenState.Ready,
    modifier: Modifier = Modifier,
    padding: PaddingValues = PaddingValues(),
    lazyListState: LazyListState = rememberLazyListState(),
    onListClicked: () -> Unit = {},
    setRead: () -> Unit = {},
) {
    val readyTracker = remember(lazyListState) { ReadyTracker(lazyListState) }
    val backgroundColor = MaterialTheme.colorScheme.surface
    val overlayAlpha = animateFloatAsState(
        targetValue = if (readyTracker.finishedInitialLoading) 0f else 1f,
        animationSpec = MaterialTheme.motionScheme.slowEffectsSpec(),
    )
    Box(
        modifier.clickable(
            interactionSource = null,
            indication = null,
            onClick = onListClicked,
        ),
    ) {
        PreloadImages(lazyListState, state.images)
        LazyColumn(
            contentPadding = padding,
            state = lazyListState,
            modifier = Modifier.drawWithContent {
                drawContent()
                if (overlayAlpha.value > 0.01f) {
                    drawRect(backgroundColor, alpha = overlayAlpha.value)
                }
            },
        ) {
            items(state.images, key = { it }) {
                ListImage(
                    model = it,
                    modifier = Modifier.fillParentMaxWidth(),
                    onSuccess = readyTracker::onContentReady,
                )
            }
            item(key = "setReadEffect", contentType = "EndEffect") {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 64.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Icon(
                        painterResource(R.drawable.check_circle),
                        contentDescription = "Success indicator",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(64.dp),
                    )
                    Text("Chapter Finished", style = MaterialTheme.typography.titleLargeEmphasized)
                }
                LaunchedEffect(true) {
                    readyTracker.onContentReady()
                    setRead()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun ListImage(model: String, modifier: Modifier = Modifier, onSuccess: () -> Unit = {}) {
    val asyncPainter = rememberAsyncImagePainter(model)
    val painterState by asyncPainter.state.collectAsStateWithLifecycle()
    DisplayImageState(painterState, modifier)
    LaunchedEffect(painterState) {
        if (painterState is AsyncImagePainter.State.Success) onSuccess()
    }
}

@Composable
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
private fun DisplayImageState(state: AsyncImagePainter.State, modifier: Modifier = Modifier) {
    when (state) {
        is AsyncImagePainter.State.Success -> Image(
            painter = state.painter,
            contentDescription = null,
            contentScale = ContentScale.FillWidth,
            modifier = modifier,
        )

        is AsyncImagePainter.State.Error -> Box(
            modifier
                .aspectRatio(1f)
                .background(MaterialTheme.colorScheme.error),
        )

        else -> Box(modifier.aspectRatio(1f), contentAlignment = Alignment.Center) {
            LoadingIndicator()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun ReaderBottomBar(
    screenState: MangaChapterReaderScreenState.Ready?,
    toChapterClicked: (ChapterId) -> Unit = {},
    toggleFavorite: () -> Unit = {},
    setReadUpToHere: () -> Unit = {},
    scrollBehavior: BottomAppBarScrollBehavior? = null,
) {
    FlexibleBottomAppBar(
        scrollBehavior = scrollBehavior,
    ) {
        IconButton(onClick = toggleFavorite) {
            FavoriteToggle(
                isFavorite = screenState?.isFavorite == true,
                favoriteContentColor = LocalContentColor.current,
            )
        }
        when (screenState?.isRead) {
            true ->
                Box(
                    modifier = Modifier.minimumInteractiveComponentSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(painterResource(R.drawable.book_read), contentDescription = "Read")
                }

            else -> {
                IconButton(onClick = setReadUpToHere) {
                    Icon(
                        painter = painterResource(R.drawable.book_unread),
                        contentDescription = "Mark as read",
                    )
                }
            }
        }

        val previousChapterId = screenState?.surrounding?.previous
        IconButton(
            onClick = dropUnlessStarted { previousChapterId?.let { toChapterClicked(it) } },
            enabled = previousChapterId != null,
        ) {
            Icon(painterResource(arrow_back), null)
        }
        val nextChapterId = screenState?.surrounding?.next
        IconButton(
            onClick = dropUnlessStarted { nextChapterId?.let { toChapterClicked(it) } },
            enabled = nextChapterId != null,
        ) {
            Icon(painterResource(R.drawable.arrow_forward), null)
        }
    }
}

private class ReadyTracker(private val lazyListState: LazyListState) {
    private var readyCount by mutableIntStateOf(0)
    var finishedInitialLoading by mutableStateOf(false)
        private set

    private val visibleItemsInfo
        get() = lazyListState.layoutInfo.visibleItemsInfo

    /**
     * This does not have to be a success can also be a failure
     */
    fun onContentReady() {
        readyCount++
        if (readyCount >= visibleItemsInfo.size) {
            finishedInitialLoading = true
        }
    }
}

@OptIn(ExperimentalCoilApi::class)
@Preview("Light")
@Preview("Light - Red", wallpaper = Wallpapers.RED_DOMINATED_EXAMPLE)
@Preview("Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview("Dark - Red", uiMode = Configuration.UI_MODE_NIGHT_YES, wallpaper = Wallpapers.RED_DOMINATED_EXAMPLE)
@Composable
fun PreviewReadChapterScreen(
    @PreviewParameter(ReadChapterScreenProvider::class) data: MangaChapterReaderScreenState.Ready,
) {
    val context = LocalPlatformContext.current
    val previewHandler = remember(context) {
        AsyncImagePreviewHandler { _, request ->
            when (request.data.toString()) {
                "1" -> {
                    val image = ResourcesCompat.getDrawable(
                        context.resources,
                        R.mipmap.preview_cover_placeholder,
                        null,
                    )!!.asImage()
                    AsyncImagePainter.State.Success(image.asPainter(context), SuccessResult(image, request))
                }

                "2" -> AsyncImagePainter.State.Loading(null)
                "3" -> AsyncImagePainter.State.Error(null, ErrorResult(null, request, Throwable()))

                else -> AsyncImagePainter.State.Empty
            }
        }
    }

    CompositionLocalProvider(LocalAsyncImagePreviewHandler provides previewHandler) {
        MangaReaderTheme {
            MangaChapterReaderScreen(state = data)
        }
    }
}

class ReadChapterScreenProvider : PreviewParameterProvider<MangaChapterReaderScreenState.Ready> {
    override val values: Sequence<MangaChapterReaderScreenState.Ready>
        get() = sequenceOf(
            MangaChapterReaderScreenState.Ready(
                title = "Heavenly Martial God",
                isFavorite = false,
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
            MangaChapterReaderScreenState.Ready(
                title = "Heavenly Martial God",
                isFavorite = true,
                isRead = true,
                surrounding = SurroundingChapters(
                    previous = ChapterId(""),
                    next = ChapterId(""),
                ),
                images = persistentListOf(
                    "1",
                ),
            ),
        )
}
