package com.spiderbiggen.manhwa.data.usecase.remote

import com.spiderbiggen.manhwa.data.usecase.chapter.UpdateChapters
import com.spiderbiggen.manhwa.domain.usecase.remote.UpdateChaptersFromRemote
import javax.inject.Inject

class UpdateChaptersFromRemoteImpl @Inject constructor(
    private val updateChapters: UpdateChapters,
) : UpdateChaptersFromRemote {
    override suspend operator fun invoke(mangaId: String, skipCache: Boolean) {
        updateChapters(mangaId, skipCache)
    }
}
