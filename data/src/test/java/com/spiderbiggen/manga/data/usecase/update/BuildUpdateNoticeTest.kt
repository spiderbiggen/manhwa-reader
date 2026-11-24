package com.spiderbiggen.manga.data.usecase.update

import com.spiderbiggen.manga.data.source.local.preferences.CachedRelease
import com.spiderbiggen.manga.domain.model.update.AppVersion
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class BuildUpdateNoticeTest {
    private val build = BuildUpdateNotice()

    private fun version(raw: String) = AppVersion.parse(raw)!!

    private fun release(tag: String, body: String = "", isPreRelease: Boolean = false) =
        CachedRelease(tag, "https://github.com/releases/$tag", body, isPreRelease)

    @Test
    fun `given no newer release when building then there is no notice`() {
        val notice = build(version("1.32.4"), listOf(release("v1.32.4"), release("v1.32.3")))

        assertNull(notice)
    }

    @Test
    fun `given a newer release when building then the notice links to the latest release`() {
        val notice =
            build(
                version("1.32.2"),
                listOf(release("v1.32.4"), release("v1.32.3"), release("v1.32.2")),
            )!!

        assertEquals("1.32.4", notice.latestVersion)
        assertEquals("1.32.2", notice.currentVersion)
        assertEquals("https://github.com/releases/v1.32.4", notice.releaseUrl)
        assertEquals(listOf("1.32.4", "1.32.3"), notice.releases.map { it.version })
    }

    @Test
    fun `given unordered releases when building then they are sorted newest first`() {
        val notice =
            build(
                version("1.0.0"),
                listOf(release("v1.9.0"), release("v1.10.0"), release("v1.2.0")),
            )!!

        assertEquals(listOf("1.10.0", "1.9.0", "1.2.0"), notice.releases.map { it.version })
    }

    @Test
    fun `given a stable user when a pre-release exists then it is ignored`() {
        val releases =
            listOf(
                release("v1.33.0-rc.1"),
                release("v1.33.1", isPreRelease = true),
                release("v1.32.4"),
            )

        assertNull(build(version("1.32.4"), releases))
    }

    @Test
    fun `given a stable user when stable and pre-releases exist then only stable releases are listed`() {
        val releases = listOf(release("v1.33.0-rc.1"), release("v1.33.0"), release("v1.32.5"))

        val notice = build(version("1.32.4"), releases)!!

        assertEquals(listOf("1.33.0", "1.32.5"), notice.releases.map { it.version })
    }

    @Test
    fun `given a pre-release user when newer pre-releases and stable releases exist then all are listed`() {
        val releases = listOf(release("v1.33.0"), release("v1.33.0-rc.2"), release("v1.33.0-rc.1"))

        val notice = build(version("1.33.0-rc.1"), releases)!!

        assertEquals(listOf("1.33.0", "1.33.0-rc.2"), notice.releases.map { it.version })
    }

    @Test
    fun `given a release flagged as pre-release by github when a stable user builds then it is ignored`() {
        assertNull(build(version("1.32.4"), listOf(release("v1.33.0", isPreRelease = true))))
    }

    @Test
    fun `given a tag that is not a version when building then it is skipped`() {
        val notice = build(version("1.0.0"), listOf(release("nightly"), release("v1.1.0")))!!

        assertEquals(listOf("1.1.0"), notice.releases.map { it.version })
    }

    @Test
    fun `given many newer releases when building then the list is capped`() {
        val releases = (1..25).map { release("v1.$it.0") }

        val notice = build(version("1.0.0"), releases)!!

        assertEquals(10, notice.releases.size)
        assertEquals("1.25.0", notice.latestVersion)
    }

    @Test
    fun `given a release without user facing changes when building then it has no groups`() {
        val body =
            "### :wrench: Chores\n- [`abc1234`](https://x/y) - **deps**: bump *(PR #1 by @a)*"

        val notice = build(version("1.0.0"), listOf(release("v1.0.1", body)))!!

        assertEquals(0, notice.releases.single().groups.size)
    }
}
