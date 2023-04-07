package com.spiderbiggen.manhwa.presentation.ui.chapter.images

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.skydoves.landscapist.glide.GlideImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImagesOverview(viewModel: ImagesViewModel = viewModel()) {
    val state by viewModel.state().collectAsStateWithLifecycle(null)

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
                CircularProgressIndicator(Modifier.padding(padding))
            }

            else -> {
                LazyColumn(Modifier.padding(padding)) {
                    items(state!!.imageChunks) { index ->
                        GlideImage(
                            imageModel = { "https://api.spiderbiggen.com/manhwa/chapters/${state!!.id}/images/$index" },
                            loading = {
                                Box(
                                    Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) { CircularProgressIndicator(Modifier.padding(8.dp)) }
                            },
                            failure = {
                                Box(
                                    Modifier
                                        .height(64.dp)
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
