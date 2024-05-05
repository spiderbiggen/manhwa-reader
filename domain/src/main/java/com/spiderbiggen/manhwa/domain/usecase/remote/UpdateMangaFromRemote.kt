package com.spiderbiggen.manhwa.domain.usecase.remote

fun interface UpdateMangaFromRemote {
    suspend operator fun invoke(skipCache: Boolean)
}
