package com.spiderbiggen.manhwa.presentation.ui.chapter.overview

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage
import com.spiderbiggen.manhwa.domain.model.Chapter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChapterOverview(
    navigateToChapter: (String) -> Unit,
    viewModel: ChapterViewModel = viewModel()
) {
    LaunchedEffect(null) {
        viewModel.collect()
    }
    val state by viewModel.state.collectAsStateWithLifecycle()
    val lazyListState = rememberLazyListState()
    val topAppBarState = rememberTopAppBarState()
    ChapterOverview(navigateToChapter, state, lazyListState, topAppBarState)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChapterOverview(
    navigateToChapter: (String) -> Unit,
    state: ChapterScreenState,
    lazyListState: LazyListState = rememberLazyListState(),
    topAppBarState: TopAppBarState = rememberTopAppBarState()
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(topAppBarState)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text((state as? ChapterScreenState.Ready)?.manhwa?.title ?: "Manhwa") },
                scrollBehavior = scrollBehavior,
            )
        }
    ) { padding ->
        when (state) {
            ChapterScreenState.Loading,
            is ChapterScreenState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            is ChapterScreenState.Ready -> {
                LazyColumn(
                    Modifier
                        .padding(padding)
                        .nestedScroll(scrollBehavior.nestedScrollConnection),
                    state = lazyListState,
                ) {
                    item(contentType = "header-image") {
                        GlideImage(
                            modifier = Modifier.fillMaxWidth(),
                            imageModel = { state.manhwa.coverImage },
                            imageOptions = ImageOptions(contentScale = ContentScale.Fit),
                            loading = {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(128.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            }
                        )
                    }
                    state.manhwa.description?.let {
                        item(contentType = "header") {
                            Surface(
                                tonalElevation = 4.dp
                            ) {
                                Text(it, Modifier.padding(16.dp))
                            }
                        }
                    }

                    itemsIndexed(state.chapters, key = { _, it -> it.id }) { index, item ->
                        ChapterRow(index, item, navigateToChapter)
                    }
                }
            }
        }
    }
}

@Composable
private fun ChapterRow(
    index: Int,
    item: Chapter,
    navigateToChapter: (String) -> Unit
) {
    val title by remember(item) {
        derivedStateOf {
            StringBuilder().apply {
                append(item.number)
                item.decimal?.let {
                    append('.').append(it)
                }
                item.title?.let {
                    append(" - ").append(it)
                }
            }.toString()
        }
    }
    Surface(
        Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 48.dp)
            .clickable { navigateToChapter(item.id) },
    ) {
        Column(Modifier.fillMaxWidth()) {
            if (index > 0) {
                Divider()
            }
            Row(
                Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title)
            }
        }
    }
}
