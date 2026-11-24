package com.spiderbiggen.manga.data.usecase.update

/** What the update check needs to know about the running app. */
data class UpdateConfig(val currentVersion: String, val enabled: Boolean)
