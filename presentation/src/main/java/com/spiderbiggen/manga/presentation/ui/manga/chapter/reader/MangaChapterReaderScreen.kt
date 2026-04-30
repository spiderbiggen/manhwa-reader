package com.spiderbiggen.manga.presentation.ui.manga.chapter.reader

import android.app.Activity
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.layout.LazyLayoutCacheWindow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FlexibleBottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.PermanentDrawerSheet
import androidx.compose.material3.PermanentNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewDynamicColors
import androidx.compose.ui.tooling.preview.PreviewFontScale
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
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
import com.spiderbiggen.manga.presentation.components.ReaderScaffold
import com.spiderbiggen.manga.presentation.components.topappbar.MangaTopAppBar
import com.spiderbiggen.manga.presentation.theme.MangaReaderTheme
import com.spiderbiggen.manga.presentation.ui.main.LocalIsExpanded
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
    val isExpanded = LocalIsExpanded.current
    if (isExpanded) {
        MangaChapterReaderTabletLayout(
            state = state,
            snackbarHostState = snackbarHostState,
            onBackClick = onBackClick,
            onChapterClick = onChapterClick,
            toggleFavorite = toggleFavorite,
            setRead = setRead,
            setReadUpToHere = setReadUpToHere,
        )
    } else {
        MangaChapterReaderPhoneLayout(
            state = state,
            snackbarHostState = snackbarHostState,
            onBackClick = onBackClick,
            onChapterClick = onChapterClick,
            toggleFavorite = toggleFavorite,
            setRead = setRead,
            setReadUpToHere = setReadUpToHere,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class, ExperimentalFoundationApi::class)
@Composable
private fun MangaChapterReaderTabletLayout(
    state: MangaChapterReaderScreenState,
    snackbarHostState: SnackbarHostState,
    onBackClick: () -> Unit,
    onChapterClick: (ChapterId) -> Unit,
    toggleFavorite: () -> Unit,
    setRead: () -> Unit,
    setReadUpToHere: () -> Unit,
) {
    val lazyListState = rememberLazyListState(
        cacheWindow = LazyLayoutCacheWindow(0.33f, 0.33f),
    )
    var drawerVisible by rememberSaveable { mutableStateOf(true) }
    val topAppBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    PermanentNavigationDrawer(
        drawerContent = {
            if (drawerVisible) {
                ChapterDrawerContent(
                    state = state,
                    lazyListState = lazyListState,
                    onChapterClick = onChapterClick,
                )
            } else {
                CollapsedDrawerRail(
                    lazyListState = lazyListState,
                    onExpand = { drawerVisible = true },
                )
            }
        },
    ) {
        Scaffold(
            topBar = {
                val view = LocalView.current
                val context = LocalContext.current
                MangaTopAppBar(
                    navigationIcon = {
                        IconButton(
                            onClick = dropUnlessStarted {
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
                    title = {
                        Text(
                            text = state.title.orEmpty(),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    },
                    actions = {
                        if (state is MangaChapterReaderScreenState.Ready) {
                            IconButton(onClick = toggleFavorite) {
                                FavoriteToggle(
                                    isFavorite = state.isFavorite,
                                    favoriteContentColor = LocalContentColor.current,
                                )
                            }
                            when (state.isRead) {
                                true -> Box(
                                    modifier = Modifier.minimumInteractiveComponentSize(),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Icon(painterResource(R.drawable.book_read), "Read")
                                }

                                else -> IconButton(onClick = setReadUpToHere) {
                                    Icon(painterResource(R.drawable.book_unread), "Mark as read")
                                }
                            }
                        }
                        IconButton(onClick = { drawerVisible = !drawerVisible }) {
                            Icon(
                                painterResource(
                                    if (drawerVisible) R.drawable.chevron_backward else R.drawable.chevron_forward,
                                ),
                                contentDescription = if (drawerVisible) "Collapse drawer" else "Expand drawer",
                            )
                        }
                    },
                    scrollBehavior = topAppBarScrollBehavior,
                )
            },
            snackbarHost = { SnackbarHost(snackbarHostState) },
        ) { contentPadding ->
            Box(
                Modifier
                    .fillMaxSize()
                    .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
            ) {
                when (state) {
                    is MangaChapterReaderScreenState.Loading -> Box(
                        Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) { LoadingIndicator() }

                    is MangaChapterReaderScreenState.Ready -> {
                        BoxWithConstraints(Modifier.fillMaxSize()) {
                            val maxImageWidth = min(maxWidth * 0.8f, 480.dp)
                            ReadyImagesOverview(
                                modifier = Modifier.fillMaxSize(),
                                state = state,
                                contentPadding = contentPadding,
                                lazyListState = lazyListState,
                                maxImageWidth = maxImageWidth,
                                setRead = setRead,
                            )
                        }
                    }

                    is MangaChapterReaderScreenState.Error -> Text(state.errorMessage)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun ChapterDrawerContent(
    state: MangaChapterReaderScreenState,
    lazyListState: LazyListState,
    onChapterClick: (ChapterId) -> Unit,
) {
    PermanentDrawerSheet(Modifier.width(240.dp)) {
        Column(
            Modifier
                .fillMaxHeight()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = state.title.orEmpty(),
                style = MaterialTheme.typography.titleMedium,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
            )

            val totalItems by remember { derivedStateOf { lazyListState.layoutInfo.totalItemsCount } }
            val firstVisible by remember { derivedStateOf { lazyListState.firstVisibleItemIndex } }
            val progress = if (totalItems > 0) firstVisible.toFloat() / totalItems else 0f
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(Modifier.weight(1f))

            val readyState = state as? MangaChapterReaderScreenState.Ready
            val prevId = readyState?.surrounding?.previous
            val nextId = readyState?.surrounding?.next
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = { prevId?.let { onChapterClick(it) } },
                    enabled = prevId != null,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Icon(painterResource(arrow_back), null)
                    Text("Previous", modifier = Modifier.padding(start = 4.dp))
                }
                Button(
                    onClick = { nextId?.let { onChapterClick(it) } },
                    enabled = nextId != null,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Next", modifier = Modifier.padding(end = 4.dp))
                    Icon(painterResource(R.drawable.arrow_forward), null)
                }
            }
        }
    }
}

@Composable
private fun CollapsedDrawerRail(lazyListState: LazyListState, onExpand: () -> Unit) {
    Box(
        Modifier
            .width(36.dp)
            .fillMaxHeight(),
        contentAlignment = Alignment.TopCenter,
    ) {
        val totalItems by remember { derivedStateOf { lazyListState.layoutInfo.totalItemsCount } }
        val firstVisible by remember { derivedStateOf { lazyListState.firstVisibleItemIndex } }
        val progress = if (totalItems > 0) firstVisible.toFloat() / totalItems else 0f
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxHeight()
                .width(4.dp),
        )
        IconButton(onClick = onExpand, modifier = Modifier.padding(top = 8.dp)) {
            Icon(painterResource(R.drawable.chevron_forward), "Expand drawer")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class, ExperimentalFoundationApi::class)
@Composable
private fun MangaChapterReaderPhoneLayout(
    state: MangaChapterReaderScreenState,
    snackbarHostState: SnackbarHostState,
    onBackClick: () -> Unit,
    onChapterClick: (ChapterId) -> Unit,
    toggleFavorite: () -> Unit,
    setRead: () -> Unit,
    setReadUpToHere: () -> Unit,
) {
    val lazyListState = rememberLazyListState(
        cacheWindow = LazyLayoutCacheWindow(0.33f, 0.33f),
    )

    ReaderScaffold(
        lazyListState = lazyListState,
        topBar = {
            val view = LocalView.current
            val context = LocalContext.current
            MangaTopAppBar(
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
        },
        bottomBar = {
            if (state is MangaChapterReaderScreenState.Ready) {
                ReaderBottomBar(
                    screenState = state,
                    toChapterClicked = onChapterClick,
                    toggleFavorite = toggleFavorite,
                    setReadUpToHere = setReadUpToHere,
                )
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { contentPadding ->
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
                contentPadding = contentPadding,
                lazyListState = lazyListState,
                setRead = setRead,
            )

            is MangaChapterReaderScreenState.Error -> Text(state.errorMessage)
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun ReadyImagesOverview(
    state: MangaChapterReaderScreenState.Ready,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    lazyListState: LazyListState = rememberLazyListState(),
    maxImageWidth: androidx.compose.ui.unit.Dp? = null,
    setRead: () -> Unit = {},
) {
    val readyTracker = remember(lazyListState) { ReadyTracker(lazyListState) }
    val backgroundColor = MaterialTheme.colorScheme.surface
    val overlayAlpha = animateFloatAsState(
        targetValue = if (readyTracker.finishedInitialLoading) 0f else 1f,
        animationSpec = MaterialTheme.motionScheme.slowEffectsSpec(),
    )
    Box(modifier) {
        PreloadImages(lazyListState, state.images)
        LazyColumn(
            contentPadding = contentPadding,
            state = lazyListState,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.drawWithContent {
                drawContent()
                if (overlayAlpha.value > 0.01f) {
                    drawRect(backgroundColor, alpha = overlayAlpha.value)
                }
            },
        ) {
            items(state.images, key = { it }) {
                val imageModifier = if (maxImageWidth != null) {
                    Modifier.width(maxImageWidth)
                } else {
                    Modifier.fillParentMaxWidth()
                }
                ListImage(
                    model = it,
                    modifier = imageModifier,
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
