package com.spiderbiggen.manhwa.data.usecase

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.spiderbiggen.manhwa.data.worker.ManhwaUpdateWorker
import com.spiderbiggen.manhwa.domain.usecase.StartRemoteUpdate
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class StartRemoteUpdateImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : StartRemoteUpdate {
    override fun invoke() {
        WorkManager.getInstance(context).enqueueUniqueWork(
            "manhwa-update",
            ExistingWorkPolicy.KEEP,
            OneTimeWorkRequest.Builder(ManhwaUpdateWorker::class.java)
                .addTag("manhwa-update")
                .setConstraints(constraints)
                .build()
        )
    }

    private val constraints =
        Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
}