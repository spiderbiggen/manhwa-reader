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
import com.spiderbiggen.manhwa.data.usecase.chapter.UpdateChapters
import com.spiderbiggen.manhwa.domain.model.AppError
import com.spiderbiggen.manhwa.domain.model.Either
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class ChapterUpdateWorker @AssistedInject constructor(
    private val updateChapters: UpdateChapters,
    // worker context
    @Assisted appContext: Context,
    @Assisted private val params: WorkerParameters
) : CoroutineWorker(
    appContext, params
) {
    override suspend fun doWork(): Result {
        val mangaId = params.inputData.getString(KEY_UPDATED_MANGA_ID) ?: run {
            Log.e(TAG, "Manga id not found")
            return Result.failure()
        }
        val skipCache = params.inputData.getBoolean(KEY_SKIP_CACHE, false)
        return when (val result = updateChapters(mangaId, skipCache)) {
            is Either.Left -> Result.success()
            is Either.Right -> retryOrFail(result.right)
        }
    }

    private fun retryOrFail(err: AppError): Result = when {
        runAttemptCount < 3 -> Result.retry()
        else -> {
            Log.e(TAG, err.toString())
            Result.failure(workDataOf(KEY_OUTPUT_FAILURE_REASON to err))
        }
    }

    companion object {
        private const val UNIQUE_KEY_PREFIX = "chaptersUpdate"
        private const val KEY_SKIP_CACHE = "skipCache"
        private const val KEY_UPDATED_MANGA_ID = "updatedMangaId"
        private const val TAG = "ChapterUpdateWorker"

        private val constraints =
            Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()

        fun WorkManager.startChapterUpdate(mangaId: String, skipCache: Boolean) {
            enqueueUniqueWork(
                "$UNIQUE_KEY_PREFIX-$mangaId",
                ExistingWorkPolicy.KEEP,
                OneTimeWorkRequestBuilder<ChapterUpdateWorker>()
                    .setInputData(
                        workDataOf(
                            KEY_UPDATED_MANGA_ID to mangaId,
                            KEY_SKIP_CACHE to skipCache,
                        )
                    )
                    .addTag(MANGA_UPDATE_TAG)
                    .setConstraints(constraints)
                    .build()
            )
        }
    }
}