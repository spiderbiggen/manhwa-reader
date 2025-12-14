package com.spiderbiggen.manga.data.source.remote

import com.spiderbiggen.manga.data.source.remote.model.UserEntity

interface ProfileService {
    suspend fun getSelf(): UserEntity

    suspend fun updateImage(avatar: ByteArray)
}
