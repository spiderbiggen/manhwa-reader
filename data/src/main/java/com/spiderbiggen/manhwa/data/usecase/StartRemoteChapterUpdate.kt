package com.spiderbiggen.manhwa.data.usecase

import android.content.Context
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.spiderbiggen.manhwa.data.worker.ChapterUpdateWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class StartRemoteChapterUpdate @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    operator fun invoke(manhwaId: String) {
        val inputData = Data.Builder()
            .putString(ChapterUpdateWorker.UPDATED_MANHWA_ID, manhwaId)
            .build()
        WorkManager.getInstance(context)
            .enqueueUniqueWork(
                "manhwa-update-$manhwaId",
                ExistingWorkPolicy.KEEP,
                OneTimeWorkRequest.Builder(ChapterUpdateWorker::class.java)
                    .setInputData(inputData)
                    .addTag("manhwa-update-$manhwaId")
                    .setConstraints(constraints)
                    .build()
            )
    }

    private val constraints =
        Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
}