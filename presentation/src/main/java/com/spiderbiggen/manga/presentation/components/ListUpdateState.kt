package com.spiderbiggen.manga.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowUpward
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableCollection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun <T : Any, K : Any> UpdatedListButton(
    collection: ImmutableCollection<T>,
    key: (item: T) -> K,
    listState: LazyListState,
    modifier: Modifier = Modifier,
    scope: CoroutineScope = rememberCoroutineScope(),
) {
    var previousFirstId: K? by remember { mutableStateOf(null) }
    var isUpdated by remember { mutableStateOf(false) }
    SideEffect {
        if (!listState.canScrollBackward) {
            isUpdated = false
        }
        if (isUpdated) return@SideEffect
        val firstId = collection.firstOrNull()?.let { key(it) }
        isUpdated = previousFirstId != null && previousFirstId != firstId
        previousFirstId = firstId
    }
    AnimatedVisibility(LocalInspectionMode.current || isUpdated) {
        ElevatedButton(
            onClick = { scope.launch { listState.animateScrollToItem(0) } },
            modifier = modifier,
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Rounded.ArrowUpward, contentDescription = null)
                Text("New Updates")
            }
        }
    }
}
