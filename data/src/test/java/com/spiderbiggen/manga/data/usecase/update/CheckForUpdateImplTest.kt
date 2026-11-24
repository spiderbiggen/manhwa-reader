package com.spiderbiggen.manga.data.usecase.update

import arrow.core.Either
import com.spiderbiggen.manga.data.source.local.preferences.UpdatePreferences
import com.spiderbiggen.manga.data.source.remote.GitHubReleaseService
import com.spiderbiggen.manga.data.source.remote.model.GitHubReleaseEntity
import java.io.IOException
import kotlin.time.Clock
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Instant
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

private val NOW = Instant.parse("2026-11-24T12:00:00Z")

class CheckForUpdateImplTest {
    private val store = FakeUpdateStore()
    private val service = FakeGitHubReleaseService()

    private fun checker(
        currentVersion: String = "1.32.3",
        enabled: Boolean = true,
        now: Instant = NOW,
    ) =
        CheckForUpdateImpl(
            service = service,
            store = store,
            config = UpdateConfig(currentVersion, enabled),
            clock =
                object : Clock {
                    override fun now() = now
                },
        )

    private fun observer(currentVersion: String = "1.32.3", enabled: Boolean = true) =
        ObserveUpdateNoticeImpl(store, UpdateConfig(currentVersion, enabled), BuildUpdateNotice())

    @Test
    fun `given a newer release when checking then it is stored and observed`() = runBlocking {
        service.releases = listOf(entity("v1.32.4", body = FIX_BODY), entity("v1.32.3"))

        val result = checker()()

        assertTrue(result.isRight())
        assertEquals(NOW, store.state.value.lastCheckedAt)
        val notice = observer()().first()!!
        assertEquals("1.32.4", notice.latestVersion)
        assertEquals(listOf("Fix the thing"), notice.releases.single().groups.single().messages)
    }

    @Test
    fun `given a check within a day when checking again then github is not called`() = runBlocking {
        store.state.value = UpdatePreferences(lastCheckedAt = NOW - 23.hours)

        checker()()

        assertEquals(0, service.calls)
    }

    @Test
    fun `given a check more than a day ago when checking then github is called`() = runBlocking {
        store.state.value = UpdatePreferences(lastCheckedAt = NOW - 25.hours)

        checker()()

        assertEquals(1, service.calls)
        assertEquals(NOW, store.state.value.lastCheckedAt)
    }

    @Test
    fun `given a last check in the future when checking then github is called`() = runBlocking {
        store.state.value = UpdatePreferences(lastCheckedAt = NOW + 5.minutes)

        checker()()

        assertEquals(1, service.calls)
    }

    @Test
    fun `given a dismissed notice when a later check runs then the notice returns`() = runBlocking {
        service.releases = listOf(entity("v1.32.4"))
        checker()()
        store.dismiss()
        assertNull(observer()().first())

        checker(now = NOW + 25.hours)()

        assertEquals("1.32.4", observer()().first()!!.latestVersion)
    }

    @Test
    fun `given a dismissed notice when checked within a day then it stays hidden`() = runBlocking {
        service.releases = listOf(entity("v1.32.4"))
        checker()()
        store.dismiss()

        checker(now = NOW + 1.hours)()

        assertNull(observer()().first())
    }

    @Test
    fun `given the check is disabled when checking then nothing happens`() = runBlocking {
        checker(enabled = false)()

        assertEquals(0, service.calls)
        assertNull(observer(enabled = false)().first())
    }

    @Test
    fun `given an unparseable app version when checking then nothing happens`() = runBlocking {
        checker(currentVersion = "dev")()

        assertEquals(0, service.calls)
    }

    @Test
    fun `given github fails when checking then an error is returned and the check is not recorded`() =
        runBlocking {
            service.failure = IOException("offline")

            val result = checker()()

            assertTrue(result is Either.Left)
            assertNull(store.state.value.lastCheckedAt)
        }

    @Test
    fun `given draft releases when checking then they are ignored`() = runBlocking {
        service.releases = listOf(entity("v1.32.4", draft = true))

        checker()()

        assertTrue(store.state.value.releases.isEmpty())
    }

    @Test
    fun `given the app was updated since the check when observing then the notice disappears`() =
        runBlocking {
            service.releases = listOf(entity("v1.32.4"))
            checker()()

            assertNull(observer(currentVersion = "1.32.4")().first())
        }

    @Test
    fun `given a stable user when only a pre-release exists then no notice is shown`() =
        runBlocking {
            service.releases = listOf(entity("v1.33.0-rc.1", prerelease = true))
            checker()()

            assertNull(observer()().first())
        }

    @Test
    fun `given a cached pre-release when the installed version is a pre-release then it is offered`() =
        runBlocking {
            service.releases = listOf(entity("v1.33.0-rc.2", prerelease = true))
            checker(currentVersion = "1.33.0-rc.1")()

            assertEquals("1.33.0-rc.2", observer("1.33.0-rc.1")().first()!!.latestVersion)
            assertNull(observer("1.32.4")().first())
        }

    private fun entity(
        tag: String,
        body: String? = null,
        prerelease: Boolean = false,
        draft: Boolean = false,
    ) = GitHubReleaseEntity(tag, "https://github.com/releases/$tag", body, prerelease, draft)

    private class FakeGitHubReleaseService : GitHubReleaseService {
        var releases: List<GitHubReleaseEntity> = emptyList()
        var failure: Throwable? = null
        var calls = 0

        override suspend fun getReleases(): List<GitHubReleaseEntity> {
            calls++
            failure?.let { throw it }
            return releases
        }
    }

    private companion object {
        const val FIX_BODY =
            "### :bug: Bug Fixes\n- [`abc1234`](https://x/y) - **data**: fix the thing *(PR #1 by @a)*"
    }
}
