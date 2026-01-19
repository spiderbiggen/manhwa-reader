package com.spiderbiggen.manga.data.source.remote.impl

import com.spiderbiggen.manga.data.source.remote.AuthService
import com.spiderbiggen.manga.data.source.remote.model.auth.LoginBody
import com.spiderbiggen.manga.data.source.remote.model.auth.RefreshTokenBody
import com.spiderbiggen.manga.data.source.remote.model.auth.RegisterBody
import com.spiderbiggen.manga.data.source.remote.model.auth.SessionResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class AuthServiceImpl(private val client: HttpClient) : AuthService {

    override suspend fun register(body: RegisterBody): SessionResponse = client.post("api/v1/auth/register") {
        setBody(body)
    }.body()

    override suspend fun login(body: LoginBody): SessionResponse = client.post("api/v1/auth/login") {
        setBody(body)
    }.body()

    override suspend fun refresh(body: RefreshTokenBody): SessionResponse = client.post("api/v1/auth/refresh") {
        setBody(body)
    }.body()

    override suspend fun logout(body: RefreshTokenBody) {
        client.post("api/v1/auth/logout") {
            setBody(body)
        }
    }
}
