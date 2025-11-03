package com.spiderbiggen.manga.presentation.ui.manga.reader

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
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
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.res.ResourcesCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
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
import com.spiderbiggen.manga.domain.model.chapter.SurroundingChapters
import com.spiderbiggen.manga.domain.model.id.ChapterId
import com.spiderbiggen.manga.presentation.R
import com.spiderbiggen.manga.presentation.components.FavoriteToggle
import com.spiderbiggen.manga.presentation.components.ListImagePreloader
import com.spiderbiggen.manga.presentation.components.MangaScaffold
import com.spiderbiggen.manga.presentation.components.bottomappbar.BottomAppBarState
import com.spiderbiggen.manga.presentation.components.bottomappbar.rememberBottomAppBarState
import com.spiderbiggen.manga.presentation.components.scrollableFade
import com.spiderbiggen.manga.presentation.components.topappbar.TopAppBarState
import com.spiderbiggen.manga.presentation.components.topappbar.rememberTopAppBarState
import com.spiderbiggen.manga.presentation.theme.MangaReaderTheme
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun ReadChapterScreen(
    viewModel: ImagesViewModel = hiltViewModel(),
    imageLoader: ImageLoader,
    snackbarHostState: SnackbarHostState,
    onBackClick: () -> Unit,
    toChapterClicked: (ChapterId) -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ReadChapterScreen(
        snackbarHostState = snackbarHostState,
        imageLoader = imageLoader,
        state = state,
        onBackClick = onBackClick,
        toChapterClicked = toChapterClicked,
        toggleFavorite = dropUnlessStarted { viewModel.toggleFavorite() },
        setRead = dropUnlessStarted { viewModel.updateReadState() },
        setReadUpToHere = dropUnlessStarted { viewModel.setReadUpToHere() },
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ReadChapterScreen(
    state: ImagesScreenState,
    imageLoader: ImageLoader = SingletonImageLoader.get(LocalContext.current),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    onBackClick: () -> Unit = {},
    toChapterClicked: (ChapterId) -> Unit = {},
    toggleFavorite: () -> Unit = {},
    setRead: () -> Unit = {},
    setReadUpToHere: () -> Unit = {},
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
) {
    val lazyListState = rememberLazyListState()

    val topAppBarState = rememberTopAppBarState()
    val bottomAppBarState = rememberBottomAppBarState(lazyListState)

    val ready = state.ifReady()
    MangaScaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            ReaderTopAppBar(
                state = topAppBarState,
                title = ready?.title.orEmpty(),
                onBackClick = onBackClick,
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            ReaderBottomBar(
                state = bottomAppBarState,
                screenState = ready,
                toChapterClicked = toChapterClicked,
                toggleFavorite = toggleFavorite,
                setReadUpToHere = setReadUpToHere,
            )
        },
        topBarOffset = { topAppBarState.appBarOffset.floatValue.toInt() },
        bottomBarOffset = { bottomAppBarState.appBarOffset.floatValue.toInt() },
    ) { padding ->
        when (state) {
            is ImagesScreenState.Loading -> Box(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                LoadingIndicator()
            }

            is ImagesScreenState.Ready -> {
                val animationSpec = MaterialTheme.motionScheme.defaultSpatialSpec<Float>()
                ReadyImagesOverview(
                    state = state,
                    imageLoader = imageLoader,
                    modifier = Modifier
                        .nestedScroll(bottomAppBarState.nestedScrollConnection)
                        .nestedScroll(topAppBarState.nestedScrollConnection)
                        .scrollableFade(
                            canScrollBackward = { lazyListState.canScrollBackward },
                            canScrollForward = { lazyListState.canScrollForward },
                        ),
                    padding = padding,
                    lazyListState = lazyListState,
                    onListClicked = {
                        coroutineScope.launch {
                            lazyListState.animateScrollBy(
                                value = -topAppBarState.appBarOffset.floatValue,
                                animationSpec = animationSpec,
                            )
                        }
                        coroutineScope.launch {
                            topAppBarState.animateAppBarOffset(
                                offset = 0f,
                                animationSpec = animationSpec,
                            )
                        }
                        coroutineScope.launch {
                            bottomAppBarState.animateAppBarOffset(
                                offset = 0f,
                                animationSpec = animationSpec,
                            )
                        }
                    },
                    setRead = setRead,
                )
            }

            is ImagesScreenState.Error -> Text(state.errorMessage)
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun ReaderTopAppBar(
    state: TopAppBarState,
    title: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier
            .onSizeChanged { state.appBarHeight = it.height.toFloat() }
            .background(MaterialTheme.colorScheme.surface)
            .windowInsetsPadding(TopAppBarDefaults.windowInsets),
    ) {
        TopAppBar(
            navigationIcon = {
                IconButton(onClick = dropUnlessStarted(block = onBackClick)) {
                    Icon(Icons.AutoMirrored.Rounded.ArrowBack, "Back")
                }
            },
            title = { Text(title) },
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun ReadyImagesOverview(
    state: ImagesScreenState.Ready,
    imageLoader: ImageLoader,
    modifier: Modifier = Modifier,
    padding: PaddingValues = PaddingValues(),
    lazyListState: LazyListState = rememberLazyListState(),
    onListClicked: () -> Unit = {},
    setRead: () -> Unit = {},
) {
    LazyColumn(
        modifier
            .clickable(
                interactionSource = null,
                indication = null,
                onClick = onListClicked,
            ),
        contentPadding = padding,
        state = lazyListState,
    ) {
        items(state.images, key = { it }) {
            ListImage(it, imageLoader, Modifier.fillParentMaxWidth())
        }
        item(key = "setReadEffect", contentType = "EndEffect") {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icon(
                    Icons.Outlined.CheckCircle,
                    contentDescription = "Success indicator",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(64.dp),
                )
                Text("Chapter Finished", style = MaterialTheme.typography.titleLargeEmphasized)
            }
            LaunchedEffect(true) {
                setRead()
            }
        }
    }
    ListImagePreloader(imageLoader, lazyListState, state.images)
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
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
            LoadingIndicator()
        }
    }
}

@Composable
private fun ReaderBottomBar(
    state: BottomAppBarState,
    screenState: ImagesScreenState.Ready?,
    toChapterClicked: (ChapterId) -> Unit = {},
    toggleFavorite: () -> Unit = {},
    setReadUpToHere: () -> Unit = {},
) {
    Surface(
        Modifier
            .onSizeChanged { state.appBarHeight = it.height.toFloat() }
            .fillMaxWidth()
            .windowInsetsPadding(BottomAppBarDefaults.windowInsets),
    ) {
        Row(Modifier.padding(horizontal = 16.dp)) {
            IconButton(onClick = toggleFavorite) {
                FavoriteToggle(isFavorite = screenState?.isFavorite == true)
            }
            IconButton(onClick = setReadUpToHere) {
                Icon(
                    imageVector = when (screenState?.isRead) {
                        true -> Icons.Outlined.BookmarkAdded
                        else -> Icons.Outlined.BookmarkBorder
                    },
                    contentDescription = "Read",
                )
            }

            val previousChapterId = screenState?.surrounding?.previous
            IconButton(
                onClick = dropUnlessStarted { previousChapterId?.let { toChapterClicked(it) } },
                enabled = previousChapterId != null,
            ) {
                Icon(Icons.AutoMirrored.Rounded.KeyboardArrowLeft, null)
            }
            val nextChapterId = screenState?.surrounding?.next
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
                val image = ResourcesCompat.getDrawable(
                    context.resources,
                    R.mipmap.preview_cover_placeholder,
                    null,
                )!!.asImage()
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
