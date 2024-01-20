package com.spiderbiggen.manhwa.data.usecase.remote

import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.spiderbiggen.manhwa.data.KEY_OUTPUT_FAILURE_REASON
import com.spiderbiggen.manhwa.data.MANGA_UPDATE_TAG
import com.spiderbiggen.manhwa.domain.model.AppError
import com.spiderbiggen.manhwa.domain.model.Either
import com.spiderbiggen.manhwa.domain.usecase.remote.GetUpdatingState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Provider

class GetUpdatingStateImpl @Inject constructor(
    private val workManager: Provider<WorkManager>,
) : GetUpdatingState {
    override fun invoke(): Flow<Either<Boolean, AppError>> = workManager.get()
        .getWorkInfosByTagFlow(MANGA_UPDATE_TAG)
        .map(::mapWorkerInfos)

    private fun mapWorkerInfos(workerInfos: List<WorkInfo>): Either<Boolean, AppError> {
        workerInfos.firstNotNullOfOrNull(::getAppError)?.let {
            return Either.Right(it)
        }

        return Either.Left(workerInfos.any { it.state != WorkInfo.State.SUCCEEDED })
    }

    private fun getAppError(workInfo: WorkInfo): AppError? = when (workInfo.state) {
        WorkInfo.State.FAILED -> workInfo.outputData.keyValueMap[KEY_OUTPUT_FAILURE_REASON] as? AppError
        else -> null
    }
}