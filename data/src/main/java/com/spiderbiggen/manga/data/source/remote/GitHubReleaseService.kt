package com.spiderbiggen.manga.data.source.remote

import com.spiderbiggen.manga.data.source.remote.model.GitHubReleaseEntity

interface GitHubReleaseService {
    /** Published releases of the app, newest first. */
    suspend fun getReleases(): List<GitHubReleaseEntity>
}
