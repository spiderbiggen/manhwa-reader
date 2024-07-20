package com.spiderbiggen.manga.presentation.extensions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.plus

val ViewModel.defaultScope: CoroutineScope
    get() = viewModelScope + Dispatchers.Default
