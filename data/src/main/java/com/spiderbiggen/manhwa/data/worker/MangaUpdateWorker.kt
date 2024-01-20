package com.spiderbiggen.manhwa.data.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.spiderbiggen.manhwa.data.KEY_OUTPUT_FAILURE_REASON
import com.spiderbiggen.manhwa.data.MANGA_UPDATE_TAG
import com.spiderbiggen.manhwa.data.repository.FavoritesRepository
import com.spiderbiggen.manhwa.data.source.local.repository.MangaRepository
import com.spiderbiggen.manhwa.data.usecase.manga.UpdateManga
import com.spiderbiggen.manhwa.domain.model.AppError
import com.spiderbiggen.manhwa.domain.model.leftOrElse
import com.spiderbiggen.manhwa.domain.usecase.remote.StartRemoteChapterUpdate
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import javax.inject.Provider

@HiltWorker
class MangaUpdateWorker @AssistedInject constructor(
    private val updateManga: UpdateManga,
    private val favoritesRepository: FavoritesRepository,
    private val mangaRepository: MangaRepository,
    private val chapterUpdaterProvider: Provider<StartRemoteChapterUpdate>,
    // worker context
    @Assisted appContext: Context,
    @Assisted private val params: WorkerParameters
) : CoroutineWorker(
    appContext, params
) {
    override suspend fun doWork(): Result {
        val skipCache = params.inputData.getBoolean(KEY_SKIP_CACHE, false)

        updateManga(skipCache).leftOrElse { return retryOrFail(it) }
        val favorites = favoritesRepository.getFavorites()
        val mangas = mangaRepository.getMangaForUpdate()
            .getOrElse { return Result.success() }
            .intersect(favorites)

        val chapterUpdater = chapterUpdaterProvider.get()
        mangas.forEach {
            chapterUpdater(it, skipCache = skipCache)
        }
        return Result.success()
    }

    private fun retryOrFail(err: AppError): Result = when {
        runAttemptCount < 5 -> Result.retry()
        else -> {
            Log.e(TAG, err.toString())
            Result.failure(workDataOf(KEY_OUTPUT_FAILURE_REASON to err))
        }
    }

    companion object {
        private const val WORKER_UNIQUE_ID = "manga-update"
        private const val KEY_SKIP_CACHE = "skip_cache"
        private const val TAG = "MangaUpdateWorker"


        private val constraints =
            Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()

        fun WorkManager.startMangaUpdate(skipCache: Boolean) {
            enqueueUniqueWork(
                WORKER_UNIQUE_ID,
                ExistingWorkPolicy.KEEP,
                OneTimeWorkRequestBuilder<MangaUpdateWorker>()
                    .setInputData(workDataOf(KEY_SKIP_CACHE to skipCache))
                    .addTag(MANGA_UPDATE_TAG)
                    .setConstraints(constraints)
                    .build()
            )
        }
    }
}