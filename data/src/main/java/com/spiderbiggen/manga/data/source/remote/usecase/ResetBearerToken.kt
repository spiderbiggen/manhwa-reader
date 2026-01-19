package com.spiderbiggen.manga.data.source.remote.usecase

import io.ktor.client.HttpClient
import io.ktor.client.plugins.auth.authProvider
import io.ktor.client.plugins.auth.providers.BearerAuthProvider

class ResetBearerToken(private val httpClient: HttpClient) {
    operator fun invoke() {
        httpClient.authProvider<BearerAuthProvider>()?.clearToken()
    }
}
