package com.spiderbiggen.manga.data.source.remote.impl

import com.spiderbiggen.manga.data.source.remote.GitHubReleaseService
import com.spiderbiggen.manga.data.source.remote.model.GitHubReleaseEntity
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

private const val GITHUB_API_URL = "https://api.github.com/"
private const val RELEASES_PATH = "repos/spiderbiggen/manhwa-reader/releases"
private const val RELEASES_PAGE_SIZE = 30

class GitHubReleaseServiceImpl(private val client: HttpClient) : GitHubReleaseService {

    override suspend fun getReleases(): List<GitHubReleaseEntity> =
        client.get(RELEASES_PATH) { parameter("per_page", RELEASES_PAGE_SIZE) }.body()
}

/**
 * A dedicated client for the public GitHub API. It deliberately does not reuse the backend client,
 * which attaches the bearer tokens of the signed in user and other backend specific defaults.
 */
fun createGitHubClient(json: Json): HttpClient =
    HttpClient(OkHttp) {
        expectSuccess = true

        defaultRequest {
            url(GITHUB_API_URL)
            header(HttpHeaders.Accept, "application/vnd.github+json")
        }

        install(ContentNegotiation) { json(json) }

        install(HttpTimeout) {
            requestTimeoutMillis = 15_000
            connectTimeoutMillis = 5_000
            socketTimeoutMillis = 5_000
        }
    }
