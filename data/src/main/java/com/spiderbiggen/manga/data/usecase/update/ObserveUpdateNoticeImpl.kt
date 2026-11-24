package com.spiderbiggen.manga.data.usecase.update

import com.spiderbiggen.manga.data.source.local.repository.UpdateStore
import com.spiderbiggen.manga.domain.model.update.AppVersion
import com.spiderbiggen.manga.domain.model.update.UpdateNotice
import com.spiderbiggen.manga.domain.usecase.update.ObserveUpdateNotice
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class ObserveUpdateNoticeImpl(
    private val store: UpdateStore,
    private val config: UpdateConfig,
    private val buildUpdateNotice: BuildUpdateNotice,
) : ObserveUpdateNotice {

    override fun invoke(): Flow<UpdateNotice?> {
        val current = AppVersion.parse(config.currentVersion)
        return store
            .observe()
            .map { preferences ->
                if (!config.enabled || current == null || preferences.dismissed) {
                    null
                } else {
                    buildUpdateNotice(current, preferences.releases)
                }
            }
            .catch { emit(null) }
    }
}
