package com.spiderbiggen.manga.data.usecase.update

import arrow.core.Either
import arrow.core.raise.either
import com.spiderbiggen.manga.data.source.local.preferences.CachedRelease
import com.spiderbiggen.manga.data.source.local.repository.UpdateStore
import com.spiderbiggen.manga.data.source.remote.GitHubReleaseService
import com.spiderbiggen.manga.data.usecase.appError
import com.spiderbiggen.manga.domain.model.AppError
import com.spiderbiggen.manga.domain.model.update.AppVersion
import com.spiderbiggen.manga.domain.usecase.update.CheckForUpdate
import kotlin.time.Clock
import kotlin.time.Duration.Companion.hours

private val CHECK_INTERVAL = 24.hours

class CheckForUpdateImpl(
    private val service: GitHubReleaseService,
    private val store: UpdateStore,
    private val config: UpdateConfig,
    private val clock: Clock = Clock.System,
) : CheckForUpdate {

    override suspend fun invoke(): Either<AppError, Unit> = either {
        if (!config.enabled) return@either
        // Without a comparable version no notice can ever be built, so skip the request.
        AppVersion.parse(config.currentVersion) ?: return@either

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
