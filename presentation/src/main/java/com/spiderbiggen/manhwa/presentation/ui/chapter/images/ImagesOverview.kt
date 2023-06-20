package com.spiderbiggen.manhwa.presentation.ui.chapter.images

import androidx.compose.foundation.Indication
import androidx.compose.foundation.IndicationInstance
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.SubcomposeAsyncImage
import com.google.accompanist.systemuicontroller.SystemUiController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
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
    val scope = rememberCoroutineScope()
    val systemUiController = rememberSystemUiController()
    LaunchedEffect(true) {
        viewModel.collect()
    }
    val state by viewModel.state.collectAsStateWithLifecycle()

    ImagesOverview(
        onBackClick = onBackClick,
        toChapterClicked = toChapterClicked,
        toggleFavorite = viewModel::toggleFavorite,
        state = state,
        scope = scope,
        systemUiController = systemUiController,
        lazyListState = lazyListState,
        topAppBarState = topAppBarState
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
    systemUiController: SystemUiController = rememberSystemUiController(),
    lazyListState: LazyListState = rememberLazyListState(),
    topAppBarState: TopAppBarState = rememberTopAppBarState()
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(topAppBarState)
    DisposableEffect(systemUiController) {
        onDispose { systemUiController.isSystemBarsVisible = true }
    }

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
                padding,
                state,
                lazyListState,
                scope,
                scrollBehavior
            )

            is ImagesScreenState.Error -> Text(state.errorMessage)
        }
    }
}

@Composable
@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
private fun ReadyImagesOverview(
    padding: PaddingValues,
    state: ImagesScreenState.Ready,
    lazyListState: LazyListState,
    scope: CoroutineScope,
    scrollBehavior: TopAppBarScrollBehavior
) {
    val images by remember { derivedStateOf { state.images } }
    val interactionSource = remember { MutableInteractionSource() }
    ListImagePreloader(
        items = images,
        lazyListState = lazyListState
    )
    LazyColumn(
        Modifier
            .consumeWindowInsets(padding)
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                scope.launch {
                    lazyListState.scrollBy(-scrollBehavior.state.heightOffset)
                    scrollBehavior.state.heightOffset = 0f
                }
            }
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

private object NoIndication : Indication {
    private object NoIndicationInstance : IndicationInstance {
        override fun ContentDrawScope.drawIndication() {
            drawContent()
        }
    }

    @Composable
    override fun rememberUpdatedInstance(interactionSource: InteractionSource): IndicationInstance {
        return NoIndicationInstance
    }
}

