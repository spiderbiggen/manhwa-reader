package com.spiderbiggen.manga.data.usecase.update

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DismissUpdateNoticeImplTest {
    @Test
    fun `given a visible notice when dismissed then the store marks it dismissed`() = runBlocking {
        val store = FakeUpdateStore()
        assertFalse(store.state.value.dismissed)

        val result = DismissUpdateNoticeImpl(store)()

        assertTrue(result.isRight())
        assertTrue(store.state.value.dismissed)
    }
}
