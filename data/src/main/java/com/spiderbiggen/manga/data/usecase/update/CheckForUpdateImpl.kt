package com.spiderbiggen.manga.data.usecase.update

import android.util.Log
import arrow.core.Either
import arrow.core.raise.either
import com.spiderbiggen.manga.data.source.local.preferences.CachedRelease
import com.spiderbiggen.manga.data.source.local.repository.UpdateStore
import com.spiderbiggen.manga.data.source.remote.GitHubReleaseService
import com.spiderbiggen.manga.data.usecase.appError
import com.spiderbiggen.manga.domain.model.AppError
import com.spiderbiggen.manga.domain.usecase.update.CheckForUpdate
import kotlin.time.Clock
import kotlin.time.Duration.Companion.hours

private const val TAG = "CheckForUpdateImpl"
private val CHECK_INTERVAL = 24.hours

class CheckForUpdateImpl(
    private val service: GitHubReleaseService,
    private val store: UpdateStore,
    private val config: UpdateConfig,
    private val clock: Clock,
) : CheckForUpdate {

    // Callers deliberately ignore a failed check, so this is the only place it is reported.
    override suspend fun invoke(): Either<AppError, Unit> =
        check().onLeft { Log.w(TAG, "failed to check for an update", it) }

    private suspend fun check(): Either<AppError, Unit> = either {
        // Without a comparable version no notice can ever be built, so skip the request.
        if (config.versionToCheck == null) return@either

        val now = clock.now()
        val lastCheckedAt = store.getLastCheckedAt().bind()
        if (lastCheckedAt != null && lastCheckedAt <= now && now - lastCheckedAt < CHECK_INTERVAL) {
            return@either
        }

        val releases = appError { service.getReleases() }
        val cached =
            releases
                .filterNot { it.draft }
                .map { CachedRelease(it.tagName, it.htmlUrl, it.body.orEmpty(), it.prerelease) }
        // Cache everything; which releases matter is decided once, when the notice is observed.
        store.saveCheck(cached, now).bind()
    }
}
