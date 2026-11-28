package com.spiderbiggen.manga.data.usecase.update

import android.util.Log
import com.spiderbiggen.manga.data.source.local.repository.UpdateStore
import com.spiderbiggen.manga.domain.model.update.UpdateNotice
import com.spiderbiggen.manga.domain.usecase.update.ObserveUpdateNotice
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

private const val TAG = "ObserveUpdateNoticeImpl"

class ObserveUpdateNoticeImpl(
    private val store: UpdateStore,
    private val config: UpdateConfig,
    private val buildUpdateNotice: BuildUpdateNotice,
) : ObserveUpdateNotice {

    override fun invoke(): Flow<UpdateNotice?> {
        val current = config.versionToCheck ?: return flowOf(null)
        return store
            .observe()
            .map { preferences ->
                if (preferences.noticeDismissed) {
                    null
                } else {
                    buildUpdateNotice(current, preferences.releases)
                }
            }
            .catch {
                // A broken store must not break the manga list; it only hides the notice.
                Log.w(TAG, "failed to read the update state", it)
                emit(null)
            }
    }
}
