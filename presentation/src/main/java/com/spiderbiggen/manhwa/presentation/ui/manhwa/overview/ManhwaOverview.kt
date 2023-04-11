package com.spiderbiggen.manhwa.presentation.ui.manhwa.overview

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage
import com.spiderbiggen.manhwa.presentation.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManhwaOverview(navigateToManhwa: (String) -> Unit, viewModel: ManhwaViewModel = viewModel()) {
    LaunchedEffect(null) {
        viewModel.collect()
    }

    val state by viewModel.state.collectAsStateWithLifecycle()
    val lazyListState = rememberLazyListState()
    val topAppBarState = rememberTopAppBarState()
    ManhwaOverview(navigateToManhwa, state, lazyListState, topAppBarState)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManhwaOverview(
    navigateToManhwa: (String) -> Unit,
    state: ManhwaScreenState,
    lazyListState: LazyListState = rememberLazyListState(),
    topAppBarState: TopAppBarState = rememberTopAppBarState()
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(topAppBarState)
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Manhwa") },
                scrollBehavior = scrollBehavior
            )
        }
    ) { padding ->
        when (state) {
            is ManhwaScreenState.Error,
            ManhwaScreenState.Loading -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }

            is ManhwaScreenState.Ready -> {
                LazyColumn(
                    Modifier
                        .padding(padding)
                        .nestedScroll(scrollBehavior.nestedScrollConnection),
                    state = lazyListState,
                ) {

                    items(state.manhwa, key = { it.id }) {
                        Card(
                            Modifier
                                .padding(8.dp)
                                .fillMaxWidth()
                                .clickable { navigateToManhwa(it.id) }
                        ) {
                            Box(contentAlignment = Alignment.CenterEnd) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    Box(contentAlignment = Alignment.BottomEnd) {
                                        GlideImage(
                                            imageModel = { it.coverImage },
                                            modifier = Modifier
                                                .height(96.dp)
                                                .widthIn(max = 96.dp),
                                            imageOptions = ImageOptions(
                                                contentScale = ContentScale.Fit,
                                                requestSize = IntSize(256, 256)
                                            )
                                        )
                                        if (it.status == "Dropped") {
                                            Image(
                                                painter = painterResource(id = R.drawable.ic_warning),
                                                contentDescription = null,
                                                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.error),
                                            )
                                        }
                                    }
                                    Text(it.title)
                                }

                            }
                        }
                    }
                }
            }
        }
    }
}
