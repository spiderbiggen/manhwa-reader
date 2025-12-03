package com.spiderbiggen.manga.presentation.extensions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus

val ViewModel.defaultScope: CoroutineScope
    get() = viewModelScope + Dispatchers.Default

val ViewModel.defaultContext: CoroutineContext
    get() = viewModelScope.coroutineContext + Dispatchers.Default

fun ViewModel.suspended(block: suspend CoroutineScope.() -> Unit) {
    defaultScope.launch(block = block)
}

suspend inline fun ViewModel.launchDefault(crossinline block: suspend CoroutineScope.() -> Unit) {
    coroutineScope {
        launch(defaultContext) { block() }
    }
}
