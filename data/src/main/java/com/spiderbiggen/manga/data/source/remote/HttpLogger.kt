package com.spiderbiggen.manga.data.source.remote

import android.util.Log
import io.ktor.client.plugins.logging.Logger

class HttpLogger : Logger {
    override fun log(message: String) {
        Log.v("Http", message)
    }
}
