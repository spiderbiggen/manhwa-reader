@file:OptIn(ExperimentalLayoutApi::class)

package com.spiderbiggen.manhwa.presentation.ui.chapter.images

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.skydoves.landscapist.glide.GlideImage

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ImagesOverview(viewModel: ImagesViewModel = viewModel()) {
    val state by viewModel.state().collectAsStateWithLifecycle(null)
    val lazyListState = rememberLazyListState()

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    val title by remember(state) {
        derivedStateOf {
            state?.let { item ->
                StringBuilder("Chapter ").apply {
                    append(item.number)
                    item.decimal?.let {
                        append('.').append(it)
                    }
                    item.title?.let {
                        append(" - ").append(it)
                    }
                }.toString()
            } ?: "Chapter ?"
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier
                    .statusBarsPadding()
                    .navigationBarsPadding(),
                title = { Text(title) },
                scrollBehavior = scrollBehavior,
            )
        },
        contentWindowInsets = WindowInsets.navigationBars,
    ) { padding ->
        when (state) {
            null -> {
                CircularProgressIndicator(Modifier.padding(padding))
            }

            else -> state?.let { state ->
                LazyColumn(
                    Modifier
                        .consumeWindowInsets(padding)
                        .nestedScroll(scrollBehavior.nestedScrollConnection),
                    contentPadding = padding,
                    state = lazyListState,
                ) {
                    items(state.imageChunks) { url ->
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
        }
    }
}
