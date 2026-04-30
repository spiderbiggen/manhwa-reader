package com.spiderbiggen.manga.presentation.ui.manga.chapter.reader

import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.lazy.layout.LazyLayoutCacheWindow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FlexibleBottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.PreviewDynamicColors
import androidx.compose.ui.tooling.preview.PreviewFontScale
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
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
import com.spiderbiggen.manga.presentation.components.topappbar.MangaTopAppBar
import com.spiderbiggen.manga.presentation.theme.MangaReaderTheme
import kotlinx.collections.immutable.persistentListOf
import org.koin.androidx.compose.koinViewModel

@Composable
fun MangaChapterReaderScreen(
    viewModel: MangaChapterReaderViewModel = koinViewModel(),
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

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
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
    var barsVisible by rememberSaveable { mutableStateOf(true) }
    val lazyListState = rememberLazyListState(
        cacheWindow = LazyLayoutCacheWindow(0.33f, 0.33f),
    )

    val atExtreme by remember {
        derivedStateOf {
            val atStart = lazyListState.firstVisibleItemIndex == 0 &&
                lazyListState.firstVisibleItemScrollOffset == 0
            atStart || lastItemIsVisible(lazyListState)
        }
    }

    LaunchedEffect(lazyListState) {
        snapshotFlow { atExtreme to lazyListState.isScrollInProgress }
            .collect { (extreme, scrolling) ->
                when {
                    extreme -> barsVisible = true
                    scrolling -> barsVisible = false
                }
            }
    }

    val density = LocalDensity.current
    var topBarHeightPx by remember { mutableIntStateOf(0) }
    var bottomBarHeightPx by remember { mutableIntStateOf(0) }
    val topContentPadding = with(density) { topBarHeightPx.toDp() }
    val bottomContentPadding = with(density) { bottomBarHeightPx.toDp() }

    val view = LocalView.current
    val context = LocalContext.current
    // Use context + view as keys so the effect re-runs after Activity recreation (e.g. rotation),
    // avoiding stale window references in onDispose.
    DisposableEffect(context, view) {
        onDispose {
            (context as? Activity)?.window?.let { WindowCompat.getInsetsController(it, view) }
                ?.show(WindowInsetsCompat.Type.systemBars())
        }
    }
    LaunchedEffect(barsVisible) {
        val window = (context as? Activity)?.window ?: return@LaunchedEffect
        val controller = WindowCompat.getInsetsController(window, view)
        if (barsVisible) {
            controller.show(WindowInsetsCompat.Type.systemBars())
        } else {
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            controller.hide(WindowInsetsCompat.Type.systemBars())
        }
    }

    Box(Modifier.fillMaxSize()) {
        when (state) {
            is MangaChapterReaderScreenState.Loading -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                LoadingIndicator()
            }

            is MangaChapterReaderScreenState.Ready -> ReadyImagesOverview(
                modifier = Modifier.fillMaxSize(),
                state = state,
                contentPadding = PaddingValues(top = topContentPadding, bottom = bottomContentPadding),
                lazyListState = lazyListState,
                onListClicked = { if (!atExtreme) barsVisible = !barsVisible },
                setRead = setRead,
            )

            is MangaChapterReaderScreenState.Error -> Text(state.errorMessage)
        }
        AnimatedVisibility(
            visible = barsVisible,
            enter = fadeIn() + slideInVertically { -it },
            exit = fadeOut() + slideOutVertically { -it },
        ) {
            MangaTopAppBar(
                modifier = Modifier.onSizeChanged { topBarHeightPx = it.height },
                navigationIcon = {
                    IconButton(
                        onClick = dropUnlessStarted {
                            // Restore bars eagerly so they are visible during the exit transition.
                            (context as? Activity)?.window?.let {
                                WindowCompat.getInsetsController(it, view)
                                    .show(WindowInsetsCompat.Type.systemBars())
                            }
                            onBackClick()
                        },
                    ) {
                        Icon(painterResource(arrow_back), "Back")
                    }
                },
                title = { Text(text = state.title.orEmpty()) },
            )
        }
        if (state is MangaChapterReaderScreenState.Ready) {
            AnimatedVisibility(
                visible = barsVisible,
                enter = fadeIn() + slideInVertically { it },
                exit = fadeOut() + slideOutVertically { it },
                modifier = Modifier.align(Alignment.BottomCenter),
            ) {
                ReaderBottomBar(
                    modifier = Modifier.onSizeChanged { bottomBarHeightPx = it.height },
                    screenState = state,
                    toChapterClicked = onChapterClick,
                    toggleFavorite = toggleFavorite,
                    setReadUpToHere = setReadUpToHere,
                )
            }
        }
        // Padding keeps the snackbar above the bottom bar (same fixed offset as contentPadding).
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = bottomContentPadding),
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun ReadyImagesOverview(
    state: MangaChapterReaderScreenState.Ready,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
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
            contentPadding = contentPadding,
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
                    Text(
                        "Chapter Finished",
                        style = MaterialTheme.typography.titleLargeEmphasized,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
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
    modifier: Modifier = Modifier,
    toChapterClicked: (ChapterId) -> Unit = {},
    toggleFavorite: () -> Unit = {},
    setReadUpToHere: () -> Unit = {},
) {
    FlexibleBottomAppBar(modifier = modifier) {
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

private fun lastItemIsVisible(lazyListState: LazyListState): Boolean {
    val info = lazyListState.layoutInfo
    val lastVisibleItem = info.visibleItemsInfo.lastOrNull() ?: return false
    return lastVisibleItem.index + 1 >= info.totalItemsCount
}

private class ReadyTracker(private val lazyListState: LazyListState) {
    private var readyCount by mutableIntStateOf(0)
    var finishedInitialLoading by mutableStateOf(false)
        private set

    private val visibleItemsInfo
        get() = lazyListState.layoutInfo.visibleItemsInfo

    fun onContentReady() {
        readyCount++
        if (readyCount >= visibleItemsInfo.size) {
            finishedInitialLoading = true
        }
    }
}

@OptIn(ExperimentalCoilApi::class)
@PreviewLightDark
@PreviewDynamicColors
@PreviewFontScale
@PreviewScreenSizes
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
