package com.spiderbiggen.manga.domain.model.update

import kotlinx.collections.immutable.persistentListOf
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class UpdateNoticeTest {
    private val fixes = ChangeGroup(ChangeKind.Fix, persistentListOf("Sort the overview"))

    private fun notice(vararg releases: ReleaseChanges) =
        UpdateNotice(
            currentVersion = AppVersion(1, 32, 2),
            latestVersion = AppVersion(1, 32, 4),
            releaseUrl = "https://example.com/releases/v1.32.4",
            releases = persistentListOf(*releases),
        )

    @Test
    fun `given the change kinds when listed then features come before fixes and performance`() {
        assertEquals(
            listOf(ChangeKind.Feature, ChangeKind.Fix, ChangeKind.Performance),
            ChangeKind.entries,
        )
    }

    @Test
    fun `given a notice with releases when read then each release keeps its changes in order`() {
        val notice =
            notice(
                ReleaseChanges(AppVersion(1, 32, 4), persistentListOf(fixes)),
                ReleaseChanges(AppVersion(1, 32, 3), persistentListOf()),
            )

        assertEquals(
            listOf(AppVersion(1, 32, 4), AppVersion(1, 32, 3)),
            notice.releases.map { it.version },
        )
        assertEquals(listOf("Sort the overview"), notice.releases.first().groups.single().messages)
    }

    @Test
    fun `given a release without changes when read then it has no groups`() {
        assertTrue(ReleaseChanges(AppVersion(1, 32, 3), persistentListOf()).groups.isEmpty())
    }

    @Test
    fun `given notices with the same content when compared then they are equal`() {
        val release = ReleaseChanges(AppVersion(1, 32, 4), persistentListOf(fixes))

        assertEquals(notice(release), notice(release))
        assertEquals(notice(release).hashCode(), notice(release).hashCode())
    }

    @Test
    fun `given a notice when its release url changes then it is no longer equal`() {
        val original = notice(ReleaseChanges(AppVersion(1, 32, 4), persistentListOf(fixes)))

        assertNotEquals(original, original.copy(releaseUrl = "https://example.com/other"))
    }
}
