package com.spiderbiggen.manga.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.spiderbiggen.manga.presentation.R
import com.spiderbiggen.manga.presentation.components.animation.ExpressiveAnimatedVisibility
import kotlinx.collections.immutable.ImmutableCollection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun <T : Any> UpdatedListButton(
    collection: ImmutableCollection<T>,
    key: (item: T) -> String,
    listState: LazyListState,
    modifier: Modifier = Modifier,
    scope: CoroutineScope = rememberCoroutineScope(),
    manuallyScrolled: Boolean,
) {
    val previousFirstId = rememberSaveable { mutableStateOf<String?>(null) }
    var isUpdated: Boolean by rememberSaveable { mutableStateOf(false) }
    val isScrolled by remember { derivedStateOf { listState.canScrollBackward } }
    LaunchedEffect(isScrolled) {
        if (!listState.canScrollBackward) {
            isUpdated = false
        }
        if (isUpdated) return@LaunchedEffect
        val firstId = collection.firstOrNull()?.let { key(it) }
        isUpdated = previousFirstId.value?.let { it == firstId } == false
        previousFirstId.value = firstId
    }
    ExpressiveAnimatedVisibility(LocalInspectionMode.current || (manuallyScrolled && isUpdated)) {
        ElevatedButton(
            onClick = { scope.launch { listState.animateScrollToItem(0) } },
            modifier = modifier,
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(painterResource(R.drawable.arrow_upward), contentDescription = null)
                Text("New Updates")
            }
        }
    }
}
