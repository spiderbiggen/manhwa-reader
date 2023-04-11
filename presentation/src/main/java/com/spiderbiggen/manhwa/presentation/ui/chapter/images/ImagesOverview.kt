@file:OptIn(ExperimentalLayoutApi::class)

package com.spiderbiggen.manhwa.presentation.ui.chapter.images

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
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
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.skydoves.landscapist.glide.GlideImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImagesOverview(
    onBackClick: () -> Unit,
    toChapterClicked: (String) -> Unit,
    viewModel: ImagesViewModel = viewModel()
) {
    val scope = rememberCoroutineScope()
    LaunchedEffect(null) {
        viewModel.collect()
    }
    val state by viewModel.state.collectAsStateWithLifecycle()
    val lazyListState = rememberLazyListState()
    val topAppBarState = rememberTopAppBarState()

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
    toggleFavorite: () -> Unit,
    state: ImagesScreenState,
    scope: CoroutineScope = rememberCoroutineScope(),
    lazyListState: LazyListState = rememberLazyListState(),
    topAppBarState: TopAppBarState = rememberTopAppBarState()
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(topAppBarState)

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
                title = { Text(state.title) },
                scrollBehavior = scrollBehavior,
            )
        },
        bottomBar = {
            BottomAppBar(
                actions = {
                    IconButton(
                        onClick = toggleFavorite
                    ) {
                        Icon(
                            imageVector = if (state.isFavorite) Icons.Outlined.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Favorite"
                        )
                    }
                    IconButton(
                        onClick = { state.previous?.let { toChapterClicked(it) } },
                        enabled = state.previous != null
                    ) {
                        Icon(Icons.Rounded.KeyboardArrowLeft, null)
                    }
                    IconButton(
                        onClick = { state.next?.let { toChapterClicked(it) } },
                        enabled = state.next != null
                    ) {
                        Icon(Icons.Rounded.KeyboardArrowRight, null)
                    }
                },
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = {
                            scope.launch {
                                topAppBarState.heightOffset = 0F
                                lazyListState.animateScrollToItem(0)
                            }
                        }
                    ) {
                        Icon(Icons.Rounded.KeyboardArrowUp, null)
                    }
                }
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

            is ImagesScreenState.Ready -> {
                LazyColumn(
                    Modifier
                        .consumeWindowInsets(padding)
                        .nestedScroll(scrollBehavior.nestedScrollConnection),
                    contentPadding = padding,
                    state = lazyListState,
                ) {
                    items(state.images) { url ->
                        GlideImage(
                            imageModel = { url },
                            loading = {
                                Box(
                                    Modifier
                                        .fillMaxWidth()
                                        .height(360.dp),
                                    contentAlignment = Alignment.Center
                                ) { CircularProgressIndicator(Modifier.padding(8.dp)) }
                            },
                            failure = {
                                Box(
                                    Modifier
                                        .fillMaxWidth()
                                        .height(360.dp)
                                        .background(MaterialTheme.colorScheme.error)
                                )
                            }
                        )
                    }
                }
            }

            else -> {}
        }
    }
}
