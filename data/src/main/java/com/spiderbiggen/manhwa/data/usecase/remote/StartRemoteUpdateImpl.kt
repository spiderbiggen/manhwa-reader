package com.spiderbiggen.manhwa.data.usecase.remote

import androidx.work.WorkManager
import com.spiderbiggen.manhwa.data.worker.MangaUpdateWorker.Companion.startMangaUpdate
import com.spiderbiggen.manhwa.domain.usecase.remote.StartRemoteUpdate
import javax.inject.Inject
import javax.inject.Provider

class StartRemoteUpdateImpl @Inject constructor(
    private val workManager: Provider<WorkManager>
) : StartRemoteUpdate {
    override fun invoke(skipCache: Boolean) {
        workManager.get().startMangaUpdate(skipCache = skipCache)
    }
}