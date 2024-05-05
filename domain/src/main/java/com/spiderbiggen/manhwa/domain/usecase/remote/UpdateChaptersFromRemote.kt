package com.spiderbiggen.manhwa.domain.usecase.remote

fun interface UpdateChaptersFromRemote {
    suspend operator fun invoke(mangaId: String, skipCache: Boolean)
}
