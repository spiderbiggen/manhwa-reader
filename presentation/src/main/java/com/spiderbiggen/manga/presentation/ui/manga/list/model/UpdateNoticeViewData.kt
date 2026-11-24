package com.spiderbiggen.manga.presentation.ui.manga.list.model

import androidx.compose.runtime.Immutable
import com.spiderbiggen.manga.domain.model.update.ChangeGroup
import com.spiderbiggen.manga.domain.model.update.ChangeKind
import com.spiderbiggen.manga.domain.model.update.ReleaseChanges
import com.spiderbiggen.manga.domain.model.update.UpdateNotice
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

@Immutable
data class UpdateNoticeViewData(
    val currentVersion: String,
    val latestVersion: String,
    val releaseUrl: String,
    val releases: ImmutableList<ReleaseChangesViewData>,
)

/** Empty [groups] means the release has no user facing changes. */
@Immutable
data class ReleaseChangesViewData(
    val version: String,
    val groups: ImmutableList<ChangeGroupViewData>,
)

@Immutable data class ChangeGroupViewData(val kind: ChangeKind, val messages: ImmutableList<String>)

fun UpdateNotice.toViewData() =
    UpdateNoticeViewData(
        currentVersion = currentVersion,
        latestVersion = latestVersion,
        releaseUrl = releaseUrl,
        releases = releases.map(ReleaseChanges::toViewData).toImmutableList(),
    )

private fun ReleaseChanges.toViewData() =
    ReleaseChangesViewData(
        version = version,
        groups = groups.map(ChangeGroup::toViewData).toImmutableList(),
    )

private fun ChangeGroup.toViewData() = ChangeGroupViewData(kind, messages)
