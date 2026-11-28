package com.spiderbiggen.manga.presentation.ui.manga.list.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.spiderbiggen.manga.presentation.R
import com.spiderbiggen.manga.presentation.theme.MangaReaderTheme
import com.spiderbiggen.manga.presentation.ui.manga.list.model.UpdateNoticeViewData
import kotlinx.collections.immutable.persistentListOf

@Composable
fun UpdateNoticeCard(
    notice: UpdateNoticeViewData,
    onWhatsNewClick: () -> Unit,
    onDismissClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
            ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = stringResource(R.string.update_notice_title),
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text =
                    stringResource(
                        R.string.update_notice_summary,
                        notice.latestVersion,
                        notice.currentVersion,
                    ),
                style = MaterialTheme.typography.bodyMedium,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp, alignment = Alignment.End),
            ) {
                TextButton(onClick = onDismissClick) {
                    Text(stringResource(R.string.update_notice_dismiss))
                }
                Button(onClick = onWhatsNewClick) {
                    Text(stringResource(R.string.update_notice_whats_new))
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun PreviewUpdateNoticeCard() {
    MangaReaderTheme {
        UpdateNoticeCard(
            notice =
                UpdateNoticeViewData(
                    currentVersion = "1.32.2",
                    latestVersion = "1.32.4",
                    releaseUrl = "https://github.com/spiderbiggen/manhwa-reader/releases",
                    releases = persistentListOf(),
                ),
            onWhatsNewClick = {},
            onDismissClick = {},
            modifier = Modifier.padding(8.dp),
        )
    }
}
