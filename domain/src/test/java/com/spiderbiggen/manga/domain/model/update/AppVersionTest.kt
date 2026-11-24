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
        listOf("", "latest", "1.2", "1.2.x", "v1..3", "1.2.3.4", "v", "-").forEach {
            assertNull(it, AppVersion.parse(it))
        }
    }

    @Test
    fun `given numbers that are not plain digits when parsed then result is null`() {
        listOf("+1.2.3", "1.+2.3", "1.2.-3", "1. 2.3", "99999999999.0.0").forEach {
            assertNull(it, AppVersion.parse(it))
        }
    }

    @Test
    fun `given a malformed pre-release when parsed then result is null`() {
        listOf("1.0.0-", "1.0.0-rc..1", "1.0.0-rc.", "1.0.0-r_c", "1.0.0+build").forEach {
            assertNull(it, AppVersion.parse(it))
        }
    }

    @Test
    fun `given a pre-release with hyphens in an identifier when parsed then they are kept`() {
        assertEquals(listOf("alpha-1", "2"), AppVersion.parse("1.0.0-alpha-1.2")?.preRelease)
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

        assertEquals(ordered, ordered.reversed().sorted())
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

    @Test
    fun `given a release without a pre-release when formatted then only the core is shown`() {
        assertEquals("1.32.4", AppVersion(1, 32, 4).toString())
    }

    @Test
    fun `given a numeric and an alphanumeric identifier when compared then numeric is older`() {
        val numeric = AppVersion.parse("1.0.0-1")!!
        val alphanumeric = AppVersion.parse("1.0.0-alpha")!!

        assertTrue(alphanumeric > numeric)
        assertTrue(numeric < alphanumeric)
    }

    @Test
    fun `given a pre-release and a longer one with the same prefix when compared then longer is newer`() {
        assertTrue(AppVersion.parse("1.0.0-alpha.1")!! > AppVersion.parse("1.0.0-alpha")!!)
    }

    @Test
    fun `given upper case letters in a pre-release when parsed then they are accepted`() {
        assertEquals(listOf("RC", "1"), AppVersion.parse("1.0.0-RC.1")?.preRelease)
    }
}
