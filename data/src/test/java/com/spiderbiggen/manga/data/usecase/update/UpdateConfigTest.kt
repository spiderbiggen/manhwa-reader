package com.spiderbiggen.manga.data.usecase.update

import com.spiderbiggen.manga.domain.model.update.AppVersion
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class UpdateConfigTest {
    private val version = AppVersion.parse("1.32.3")

    @Test
    fun `given an enabled check and a comparable version when asked then the version is returned`() {
        assertEquals(version, UpdateConfig(version, enabled = true).versionToCheck)
    }

    @Test
    fun `given a disabled check when asked then there is no version to check`() {
        assertNull(UpdateConfig(version, enabled = false).versionToCheck)
    }

    @Test
    fun `given a version that cannot be compared when asked then there is no version to check`() {
        assertNull(UpdateConfig(currentVersion = null, enabled = true).versionToCheck)
    }
}
