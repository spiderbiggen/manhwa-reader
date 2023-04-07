package com.spiderbiggen.manhwa.presentation.ui.chapter.overview

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChapterOverview(navigateToChapter: (String) -> Unit, viewModel: ChapterViewModel = viewModel()) {
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
                    items(state!!.second, key = { it.id }) { item ->
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
                        Card(
                            Modifier
                                .padding(8.dp)
                                .fillMaxWidth()
                                .heightIn(min = 48.dp)
                                .clickable {
                                    navigateToChapter(item.id)
                                }
                        ) {
                            Row(
                                Modifier
                                    .weight(1f)
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(title)
                            }
                        }
                    }
                }
            }
        }
    }
}
