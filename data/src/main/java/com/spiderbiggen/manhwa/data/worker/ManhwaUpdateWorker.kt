package com.spiderbiggen.manhwa.data.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.spiderbiggen.manhwa.data.repository.FavoritesRepository
import com.spiderbiggen.manhwa.data.source.local.repository.ManhwaRepository
import com.spiderbiggen.manhwa.data.usecase.StartRemoteChapterUpdate
import com.spiderbiggen.manhwa.domain.model.AppError
import com.spiderbiggen.manhwa.domain.model.leftOrElse
import com.spiderbiggen.manhwa.domain.usecase.manhwa.UpdateManhwa
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class ManhwaUpdateWorker @AssistedInject constructor(
    private val updateManhwa: UpdateManhwa,
    private val favoritesRepository: FavoritesRepository,
    private val manhwaRepository: ManhwaRepository,
    private val startRemoteChapterUpdate: StartRemoteChapterUpdate,
    // worker context
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters
) : CoroutineWorker(
    appContext, params
) {
    override suspend fun doWork(): Result {
        updateManhwa().leftOrElse { return retryOrFail(it) }
        val favorites = favoritesRepository.getFavorites()
        val manhwas = manhwaRepository.getManhwaForUpdate()
            .getOrElse { return Result.success() }
            .intersect(favorites)

        manhwas.forEach {
            startRemoteChapterUpdate(it)
        }
        return Result.success()
    }

    private fun retryOrFail(err: AppError): Result = when {
        runAttemptCount < 5 -> Result.retry()
        else -> {
            Log.e(TAG, err.toString())
            Result.failure()
        }
    }

    companion object {
        const val TAG = "ManhwaUpdateWorker"
    }
}