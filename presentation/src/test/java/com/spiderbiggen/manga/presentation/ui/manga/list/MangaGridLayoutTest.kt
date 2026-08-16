package com.spiderbiggen.manga.presentation.ui.manga.list

import androidx.compose.ui.unit.dp
import org.junit.Assert.assertEquals
import org.junit.Test

class MangaGridLayoutTest {
    @Test
    fun `given a tablet width when minimum card width is resolved then tablet size is used`() {
        assertEquals(180.dp, mangaGridMinCardWidth(600.dp))
    }

    @Test
    fun `given a phone width when minimum card width is resolved then phone size is used`() {
        assertEquals(110.dp, mangaGridMinCardWidth(599.dp))
    }
}
