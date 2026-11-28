package com.spiderbiggen.manga.data.source.remote.model

import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GitHubReleaseEntityTest {
    private val json = Json { ignoreUnknownKeys = true }

    private fun decode(text: String) = json.decodeFromString<GitHubReleaseEntity>(text)

    private val required = """"tag_name":"v1.0.0","html_url":"https://github.com/r/v1.0.0""""

    @Test
    fun `given each optional field on its own when decoded then only that field is set`() {
        val base = GitHubReleaseEntity("v1.0.0", "https://github.com/r/v1.0.0")

        assertEquals(base.copy(body = "notes"), decode("{$required,\"body\":\"notes\"}"))
        assertEquals(base.copy(prerelease = true), decode("{$required,\"prerelease\":true}"))
        assertEquals(base.copy(draft = true), decode("{$required,\"draft\":true}"))
    }

    @Test
    fun `given the fields in a different order when decoded then the same release is read`() {
        val ordered =
            decode(
                """{"tag_name":"v2.0.0","html_url":"https://x","body":"b","prerelease":true,"draft":true}"""
            )
        val reversed =
            decode(
                """{"draft":true,"prerelease":true,"body":"b","html_url":"https://x","tag_name":"v2.0.0"}"""
            )

        assertEquals(ordered, reversed)
        assertEquals(
            GitHubReleaseEntity("v2.0.0", "https://x", "b", prerelease = true, draft = true),
            ordered,
        )
    }

    @Test
    fun `given a release without a required field when decoded then it is rejected`() {
        listOf(
                """{"html_url":"https://x"}""",
                """{"tag_name":"v1.0.0"}""",
                """{"body":"only optional"}""",
            )
            .forEach { text ->
                val failure = runCatching { decode(text) }.exceptionOrNull()

                assertTrue(text, failure is SerializationException)
            }
    }

    @Test
    fun `given a release when encoded and decoded then it round trips`() {
        val release =
            GitHubReleaseEntity("v1.0.0", "https://x", "b", prerelease = true, draft = false)

        assertEquals(release, decode(json.encodeToString(release)))
    }
}
