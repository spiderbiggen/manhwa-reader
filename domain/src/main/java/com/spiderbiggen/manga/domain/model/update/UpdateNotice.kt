package com.spiderbiggen.manga.domain.model.update

import kotlinx.collections.immutable.ImmutableList

/** A newer release than the one currently installed, with what changed in every release since. */
data class UpdateNotice(
    val currentVersion: String,
    val latestVersion: String,
    val releaseUrl: String,
    val releases: ImmutableList<ReleaseChanges>,
)

/** The user-visible changes of a single release. Empty [groups] means a maintenance release. */
data class ReleaseChanges(val version: String, val groups: ImmutableList<ChangeGroup>)

data class ChangeGroup(val kind: ChangeKind, val messages: ImmutableList<String>)

enum class ChangeKind {
    Feature,
    Fix,
    Performance,
}
