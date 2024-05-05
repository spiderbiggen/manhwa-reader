package com.spiderbiggen.manhwa.data.usecase.remote

import com.spiderbiggen.manhwa.data.usecase.manga.UpdateManga
import com.spiderbiggen.manhwa.domain.usecase.remote.UpdateMangaFromRemote
import javax.inject.Inject

class UpdateMangaFromRemoteImpl @Inject constructor(
    private val updateManga: UpdateManga,
) : UpdateMangaFromRemote {
    override suspend operator fun invoke(skipCache: Boolean) {
        updateManga(skipCache)
    }
}
