package com.spiderbiggen.manhwa.data.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.spiderbiggen.manhwa.domain.model.Either
import com.spiderbiggen.manhwa.domain.usecase.chapter.UpdateChapters
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
        val manhwaId = params.inputData.getString(UPDATED_MANHWA_ID) ?: run {
            Log.e(TAG, "Manhwa id not found")
            return Result.failure()
        }
        return when (updateChapters(manhwaId)) {
            is Either.Left -> Result.success()
            is Either.Right -> when {
                runAttemptCount < 5 -> Result.retry()
                else -> Result.failure()
            }
        }
    }

    companion object {
        const val UPDATED_MANHWA_ID = "UpdatedManhwaId"
        const val TAG = "ChapterUpdateWorker"
    }
}