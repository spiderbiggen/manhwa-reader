package com.spiderbiggen.manga.data.usecase.update

import com.spiderbiggen.manga.data.source.local.preferences.CachedRelease
import com.spiderbiggen.manga.domain.model.update.AppVersion
import com.spiderbiggen.manga.domain.model.update.ReleaseChanges
import com.spiderbiggen.manga.domain.model.update.UpdateNotice
import kotlinx.collections.immutable.toImmutableList

private const val MAX_RELEASES = 10

/** Decides which releases matter to the installed version and describes them. */
class BuildUpdateNotice(private val parser: ReleaseNotesParser) {

    /**
     * Describes the newest releases above [current], newest first. Pre-releases are only relevant
     * to users who already run a pre-release.
     */
    operator fun invoke(current: AppVersion, releases: List<CachedRelease>): UpdateNotice? {
        val candidates = candidates(current, releases)
        val latest = candidates.firstOrNull() ?: return null
        return UpdateNotice(
            currentVersion = current,
            latestVersion = latest.version,
            releaseUrl = latest.release.url,
            releases =
                candidates
                    .map { ReleaseChanges(it.version, parser.parse(it.release.body)) }
                    .toImmutableList(),
        )
    }

    private fun candidates(current: AppVersion, releases: List<CachedRelease>): List<Candidate> =
        releases
            .mapNotNull { release ->
                val version = AppVersion.parse(release.tagName) ?: return@mapNotNull null
                val visible =
                    current.isPreRelease || !(version.isPreRelease || release.isPreRelease)
                if (version > current && visible) Candidate(version, release) else null
            }
            .sortedByDescending { it.version }
            .take(MAX_RELEASES)

    /** A release that is newer than the installed version, with its parsed version. */
    private data class Candidate(val version: AppVersion, val release: CachedRelease)
}
