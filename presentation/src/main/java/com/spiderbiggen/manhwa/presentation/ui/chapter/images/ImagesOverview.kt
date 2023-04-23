package com.spiderbiggen.manhwa.presentation.ui.chapter.images

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.KeyboardArrowLeft
import androidx.compose.material.icons.rounded.KeyboardArrowRight
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.SubcomposeAsyncImage
import com.spiderbiggen.manhwa.presentation.components.ListImagePreloader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImagesOverview(
    onBackClick: () -> Unit,
    toChapterClicked: (String) -> Unit,
    viewModel: ImagesViewModel = viewModel()
) {
    val lazyListState = rememberLazyListState()
    val topAppBarState = rememberTopAppBarState()
    val scope: CoroutineScope = rememberCoroutineScope()
    LaunchedEffect(true) {
        viewModel.collect()
    }
    val state by viewModel.state.collectAsStateWithLifecycle()

    ImagesOverview(
        onBackClick,
        toChapterClicked,
        viewModel::toggleFavorite,
        state,
        scope,
        lazyListState,
        topAppBarState
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ImagesOverview(
    onBackClick: () -> Unit,
    toChapterClicked: (String) -> Unit,
    toggleFavorite: suspend () -> Unit,
    state: ImagesScreenState,
    scope: CoroutineScope = rememberCoroutineScope(),
    lazyListState: LazyListState = rememberLazyListState(),
    topAppBarState: TopAppBarState = rememberTopAppBarState()
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(topAppBarState)

    val (title, isFavorite, surrounding) = remember(state) {
        when (state) {
            is ImagesScreenState.Error,
            ImagesScreenState.Loading -> Triple("", false, null)
            is ImagesScreenState.Ready -> Triple(state.title, state.isFavorite, state.surrounding)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier
                    .statusBarsPadding()
                    .navigationBarsPadding(),
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Rounded.ArrowBack, "Back")
                    }
                },
                title = { Text(title) },
                scrollBehavior = scrollBehavior,
            )
        },
        bottomBar = {
            BottomAppBar(
                actions = {
                    IconButton(
                        onClick = { scope.launch { toggleFavorite() } }
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Outlined.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Favorite"
                        )
                    }
                    IconButton(
                        onClick = { surrounding?.previous?.let { toChapterClicked(it) } },
                        enabled = surrounding?.previous != null
                    ) {
                        Icon(Icons.Rounded.KeyboardArrowLeft, null)
                    }
                    IconButton(
                        onClick = { surrounding?.next?.let { toChapterClicked(it) } },
                        enabled = surrounding?.next != null
                    ) {
                        Icon(Icons.Rounded.KeyboardArrowRight, null)
                    }
                },
            )
        },
        contentWindowInsets = WindowInsets.navigationBars,
    ) { padding ->
        when (state) {
            is ImagesScreenState.Loading -> {
                Box(
                    modifier = Modifier
                        .consumeWindowInsets(padding)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is ImagesScreenState.Ready -> ReadyImagesOverview(
                state,
                lazyListState,
                padding,
                scrollBehavior
            )

            is ImagesScreenState.Error -> {}
        }
    }
}

@Composable
@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
private fun ReadyImagesOverview(
    state: ImagesScreenState.Ready,
    lazyListState: LazyListState,
    padding: PaddingValues,
    scrollBehavior: TopAppBarScrollBehavior
) {
    val images by remember { derivedStateOf { state.images } }
    ListImagePreloader(
        items = images,
        lazyListState = lazyListState
    )
    LazyColumn(
        Modifier
            .consumeWindowInsets(padding)
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        contentPadding = padding,
        state = lazyListState,
    ) {
        items(images) {
            ListImage(it, Modifier.fillParentMaxWidth())
        }
    }
}

private val boxModifier = Modifier
    .fillMaxWidth()
    .height(360.dp)

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
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        },
        error = {
            Box(
                boxModifier.background(MaterialTheme.colorScheme.error)
            )
        }
    )
}

