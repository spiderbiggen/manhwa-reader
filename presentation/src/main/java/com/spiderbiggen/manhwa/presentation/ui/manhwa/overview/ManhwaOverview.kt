package com.spiderbiggen.manhwa.presentation.ui.manhwa.overview

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManhwaOverview(navigateToManhwa: (String) -> Unit, viewModel: ManhwaViewModel = viewModel()) {
    val state by viewModel.state().collectAsStateWithLifecycle(emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Manhwa") },
                colors = TopAppBarDefaults.mediumTopAppBarColors()
            )
        }
    ) { padding ->
        when (state) {
            null -> {
                Text("Failed to load", Modifier.padding(padding))
            }

            else -> {
                LazyColumn(Modifier.padding(padding)) {
                    items(state!!, key = { it.id }) {
                        Card(
                            Modifier
                                .padding(8.dp)
                                .fillMaxWidth()
                                .clickable { navigateToManhwa(it.id) }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
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
                                Text(it.title)
                            }
                        }
                    }
                }
            }
        }
    }
}
