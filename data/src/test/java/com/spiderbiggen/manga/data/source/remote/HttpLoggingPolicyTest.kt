package com.spiderbiggen.manga.data.source.remote

import io.ktor.client.plugins.logging.LogLevel
import org.junit.Assert.assertEquals
import org.junit.Test

class HttpLoggingPolicyTest {
    @Test
    fun `given debug logging when selecting the HTTP log level then request and response bodies are excluded`() {
        assertEquals(LogLevel.HEADERS, httpLogLevel(isDebug = true))
    }

    @Test
    fun `given release logging when selecting the HTTP log level then informational logging is used`() {
        assertEquals(LogLevel.INFO, httpLogLevel(isDebug = false))
    }
}
