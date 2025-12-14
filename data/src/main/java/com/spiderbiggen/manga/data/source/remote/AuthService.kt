package com.spiderbiggen.manga.data.source.remote

import com.spiderbiggen.manga.data.source.remote.model.auth.LoginBody
import com.spiderbiggen.manga.data.source.remote.model.auth.RefreshTokenBody
import com.spiderbiggen.manga.data.source.remote.model.auth.RegisterBody
import com.spiderbiggen.manga.data.source.remote.model.auth.SessionResponse

interface AuthService {
    suspend fun register(body: RegisterBody): SessionResponse

    suspend fun login(body: LoginBody): SessionResponse

    suspend fun refresh(body: RefreshTokenBody): SessionResponse

    suspend fun logout(body: RefreshTokenBody)
}
