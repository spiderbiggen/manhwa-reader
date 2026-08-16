package com.spiderbiggen.manga.data.source.remote

import io.ktor.client.plugins.logging.LogLevel

internal fun httpLogLevel(isDebug: Boolean): LogLevel =
    if (isDebug) {
        LogLevel.HEADERS
    } else {
        LogLevel.INFO
    }
