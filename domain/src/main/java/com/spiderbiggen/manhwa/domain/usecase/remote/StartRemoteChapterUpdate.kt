package com.spiderbiggen.manhwa.domain.usecase.remote

fun interface StartRemoteChapterUpdate {
    operator fun invoke(mangaId: String, skipCache: Boolean)
}