package com.spiderbiggen.manhwa.data.usecase.remote

import androidx.work.WorkManager
import com.spiderbiggen.manhwa.data.worker.ChapterUpdateWorker.Companion.startChapterUpdate
import com.spiderbiggen.manhwa.domain.usecase.remote.StartRemoteChapterUpdate
import javax.inject.Inject

class StartRemoteChapterUpdateImpl @Inject constructor(
    private val workManager: WorkManager,
) : StartRemoteChapterUpdate {
    override operator fun invoke(mangaId: String, skipCache: Boolean) {
        workManager.startChapterUpdate(mangaId, skipCache)
    }
}