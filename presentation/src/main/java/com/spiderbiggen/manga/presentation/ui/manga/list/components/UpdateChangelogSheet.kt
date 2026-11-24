package com.spiderbiggen.manga.presentation.ui.manga.list.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.spiderbiggen.manga.domain.model.update.ChangeKind
import com.spiderbiggen.manga.presentation.R
import com.spiderbiggen.manga.presentation.theme.MangaReaderTheme
import com.spiderbiggen.manga.presentation.ui.manga.list.model.ChangeGroupViewData
import com.spiderbiggen.manga.presentation.ui.manga.list.model.ReleaseChangesViewData
import com.spiderbiggen.manga.presentation.ui.manga.list.model.UpdateNoticeViewData
import kotlinx.collections.immutable.persistentListOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateChangelogSheet(
    notice: UpdateNoticeViewData,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        modifier = modifier,
        sheetState =
            rememberBottomSheetState(
                initialValue = SheetValue.Hidden,
                enabledValues = setOf(SheetValue.Hidden, SheetValue.Expanded),
            ),
    ) {
        UpdateChangelogContent(notice)
    }
}

@Composable
private fun UpdateChangelogContent(notice: UpdateNoticeViewData) {
    val uriHandler = LocalUriHandler.current
    Column(
        modifier =
            Modifier.fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Column {
            Text(
                text = stringResource(R.string.update_changelog_title, notice.latestVersion),
                style = MaterialTheme.typography.headlineSmall,
            )
            Text(
                text = stringResource(R.string.update_changelog_current, notice.currentVersion),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        notice.releases.forEach { release -> ReleaseSection(release) }
        Button(
            onClick = { uriHandler.openUri(notice.releaseUrl) },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(stringResource(R.string.update_changelog_view_release))
        }
    }
}

@Composable
private fun ReleaseSection(release: ReleaseChangesViewData) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = release.version, style = MaterialTheme.typography.titleMedium)
        if (release.groups.isEmpty()) {
            Text(
                text = stringResource(R.string.update_changelog_maintenance),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        release.groups.forEach { group -> GroupSection(group) }
    }
}

@Composable
private fun GroupSection(group: ChangeGroupViewData) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = stringResource(group.kind.labelResId()),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
        )
        group.messages.forEach { message ->
            Text(text = "• $message", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@StringRes
private fun ChangeKind.labelResId() =
    when (this) {
        ChangeKind.Feature -> R.string.update_change_kind_feature
        ChangeKind.Fix -> R.string.update_change_kind_fix
        ChangeKind.Performance -> R.string.update_change_kind_performance
    }

@PreviewLightDark
@Composable
private fun PreviewUpdateChangelogContent() {
    MangaReaderTheme {
        UpdateChangelogContent(
            UpdateNoticeViewData(
                currentVersion = "1.32.2",
                latestVersion = "1.32.4",
                releaseUrl = "https://github.com/spiderbiggen/manhwa-reader/releases/tag/v1.32.4",
                releases =
                    persistentListOf(
                        ReleaseChangesViewData(
                            version = "1.32.4",
                            groups =
                                persistentListOf(
                                    ChangeGroupViewData(
                                        ChangeKind.Fix,
                                        persistentListOf(
                                            "Sort manga overview by last chapter update",
                                            "Derive manga unread state from local chapters only",
                                        ),
                                    )
                                ),
                        ),
                        ReleaseChangesViewData(version = "1.32.3", groups = persistentListOf()),
                    ),
            )
        )
    }
}
