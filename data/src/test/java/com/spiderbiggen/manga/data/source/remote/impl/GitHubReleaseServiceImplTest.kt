package com.spiderbiggen.manga.data.source.remote.impl

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GitHubReleaseServiceImplTest {
    private fun service(body: String, onRequest: (String) -> Unit = {}): GitHubReleaseServiceImpl {
        val engine = MockEngine { request ->
            onRequest(request.url.toString())
            respond(
                content = body,
                headers =
                    headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
            )
        }
        val client =
            HttpClient(engine) {
                expectSuccess = true
                defaultRequest { url("https://api.github.com/") }
                install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
            }
        return GitHubReleaseServiceImpl(client)
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
            assertEquals(null, releases[1].body)
            assertTrue(releases[1].prerelease)
            assertFalse(releases[1].draft)
        }

    @Test
    fun `when fetched then the releases endpoint of the app repository is requested with a page size`() =
        runBlocking {
            var requested = ""

            service("[]") { requested = it }.getReleases()

            assertEquals(
                "https://api.github.com/repos/spiderbiggen/manhwa-reader/releases?per_page=30",
                requested,
            )
        }
}
