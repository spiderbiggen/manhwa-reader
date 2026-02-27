package com.spiderbiggen.manga.presentation.navigation

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey

@IgnorableReturnValue
inline fun <reified T : NavKey> NavBackStack<NavKey>.popUpTo(): Boolean {
    val index = indexOfFirst { it is T }
    if (index == -1) return false
    for (i in lastIndex downTo (index + 1)) {
        removeAt(i)
    }
    return true
}

@IgnorableReturnValue
inline fun <reified T : NavKey> NavBackStack<NavKey>.popUpToInclusive(): Boolean {
    val index = indexOfFirst { it is T }
    if (index == -1) return false
    for (i in lastIndex downTo index) {
        removeAt(i)
    }
    return true
}
