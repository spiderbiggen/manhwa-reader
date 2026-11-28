package com.spiderbiggen.manga.data.usecase.update

import com.spiderbiggen.manga.domain.model.update.AppVersion

/**
 * What the update check needs to know about the running app.
 *
 * [currentVersion] is null when the app version is not a semantic version.
 */
data class UpdateConfig(val currentVersion: AppVersion?, val enabled: Boolean) {

    /**
     * The installed version when the check should run, or null when the check is disabled or the
     * installed version cannot be compared with a release.
     */
    val versionToCheck: AppVersion?
        get() = currentVersion.takeIf { enabled }
}
