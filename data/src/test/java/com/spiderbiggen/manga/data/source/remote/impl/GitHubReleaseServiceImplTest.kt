package com.spiderbiggen.manga.data.source.remote.impl

import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.request.HttpRequestData
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class GitHubReleaseServiceImplTest {
    private val json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
    }

    /** Builds the service on the production client configuration, backed by a mock engine. */
    private fun service(
        body: String,
        status: HttpStatusCode = HttpStatusCode.OK,
        onRequest: (HttpRequestData) -> Unit = {},
    ): GitHubReleaseServiceImpl {
        val engine = MockEngine { request ->
            onRequest(request)
            respond(
                content = body,
                status = status,
                headers =
                    headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
            )
        }
        return GitHubReleaseServiceImpl { createGitHubClient(json, engine) }
    }

    @Test
    fun `given a release list when fetched then fields are mapped and unknown keys ignored`() =
        runBlocking {
            val releases =
                service(
                        """
                        [
                          {"tag_name":"v1.32.4","html_url":"https://github.com/r/v1.32.4",
                           "body":"notes","prerelease":false,"draft":false,"id":1,"assets":[]},
                          {"tag_name":"v1.33.0-rc.1","html_url":"https://github.com/r/rc","body":null,
                           "prerelease":true}
                        ]
                        """
                            .trimIndent()
                    )
                    .getReleases()

            assertEquals(listOf("v1.32.4", "v1.33.0-rc.1"), releases.map { it.tagName })
            assertEquals("https://github.com/r/v1.32.4", releases[0].htmlUrl)
            assertEquals("notes", releases[0].body)
            assertFalse(releases[0].prerelease)
            assertNull(releases[1].body)
            assertTrue(releases[1].prerelease)
            assertFalse(releases[1].draft)
        }

    @Test
    fun `given only the required fields when fetched then the optional ones use their defaults`() =
        runBlocking {
            val release =
                service("""[{"tag_name":"v1.0.0","html_url":"https://github.com/r/v1.0.0"}]""")
                    .getReleases()
                    .single()

            assertNull(release.body)
            assertFalse(release.prerelease)
            assertFalse(release.draft)
        }

    @Test
    fun `given the app repository when fetched then the releases endpoint is requested`() =
        runBlocking {
            var request: HttpRequestData? = null

            service("[]") { request = it }.getReleases()

            assertEquals(
                "https://api.github.com/repos/spiderbiggen/manhwa-reader/releases?per_page=30",
                request?.url.toString(),
            )
        }

    @Test
    fun `when fetched then the request asks for the github json media type`() = runBlocking {
        var request: HttpRequestData? = null

        service("[]") { request = it }.getReleases()

        assertEquals("application/vnd.github+json", request?.headers?.get(HttpHeaders.Accept))
    }

    @Test
    fun `given an unsuccessful response when fetched then the failure is not mapped as releases`() {
        val failure = runCatching {
            runBlocking { service("{}", HttpStatusCode.NotFound).getReleases() }
        }

        assertTrue(failure.exceptionOrNull() is ClientRequestException)
    }
}
