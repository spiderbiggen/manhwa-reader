package com.spiderbiggen.manga.domain.model.update

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AppVersionTest {
    @Test
    fun `given a plain version when parsed then components are read`() {
        assertEquals(AppVersion(1, 32, 4), AppVersion.parse("1.32.4"))
    }

    @Test
    fun `given a tag with v prefix when parsed then prefix is ignored`() {
        assertEquals(AppVersion(1, 32, 4), AppVersion.parse("v1.32.4"))
    }

    @Test
    fun `given a debug build suffix when parsed then suffix is ignored`() {
        assertEquals(AppVersion(1, 32, 4), AppVersion.parse("1.32.4-debug"))
    }

    @Test
    fun `given a pre-release version when parsed then identifiers are split`() {
        val version = AppVersion.parse("v1.7.0-rc.1")

        assertEquals(listOf("rc", "1"), version?.preRelease)
        assertTrue(version!!.isPreRelease)
    }

    @Test
    fun `given a pre-release with debug suffix when parsed then it stays a pre-release`() {
        assertEquals(AppVersion(1, 7, 0, listOf("rc", "1")), AppVersion.parse("1.7.0-rc.1-debug"))
    }

    @Test
    fun `given text that is not a semantic version when parsed then result is null`() {
        listOf("", "latest", "1.2", "1.2.x", "v1..3").forEach {
            assertNull(it, AppVersion.parse(it))
        }
    }

    @Test
    fun `given versions with different cores when compared then numeric order is used`() {
        assertTrue(AppVersion(1, 10, 0) > AppVersion(1, 9, 9))
        assertTrue(AppVersion(2, 0, 0) > AppVersion(1, 99, 99))
        assertTrue(AppVersion(1, 0, 10) > AppVersion(1, 0, 9))
    }

    @Test
    fun `given a release and its pre-release when compared then release is newer`() {
        assertTrue(AppVersion(1, 7, 0) > AppVersion.parse("1.7.0-rc.1")!!)
    }

    @Test
    fun `given pre-releases when compared then identifiers follow semver precedence`() {
        val ordered =
            listOf(
                    "1.0.0-alpha",
                    "1.0.0-alpha.1",
                    "1.0.0-alpha.beta",
                    "1.0.0-beta",
                    "1.0.0-beta.2",
                    "1.0.0-beta.11",
                    "1.0.0-rc.1",
                    "1.0.0",
                )
                .map { AppVersion.parse(it)!! }

        assertEquals(ordered, ordered.shuffled().sorted())
    }

    @Test
    fun `given equal versions when compared then they are equal`() {
        assertFalse(AppVersion(1, 2, 3) > AppVersion(1, 2, 3))
        assertEquals(0, AppVersion(1, 2, 3).compareTo(AppVersion.parse("v1.2.3")!!))
    }

    @Test
    fun `given a version when formatted then it round trips`() {
        assertEquals("1.7.0-rc.1", AppVersion.parse("v1.7.0-rc.1").toString())
    }
}
