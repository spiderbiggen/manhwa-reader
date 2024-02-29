package com.spiderbiggen.manhwa.domain.usecase.remote

fun interface StartRemoteUpdate {
    operator fun invoke(skipCache: Boolean)
}
