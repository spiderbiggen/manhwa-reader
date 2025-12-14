package com.spiderbiggen.manga.data.source.remote.impl

import com.spiderbiggen.manga.data.source.remote.ProfileService
import com.spiderbiggen.manga.data.source.remote.model.UserEntity
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.compression.compress
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import javax.inject.Inject

class ProfileServiceImpl @Inject constructor(private val client: HttpClient) : ProfileService {

    override suspend fun getSelf(): UserEntity = client.get("api/v1/users/me").body()

    override suspend fun updateImage(avatar: ByteArray) {
        client.post("api/v1/users/profile/avatar") {
            compress("gzip")
            setBody(
                MultiPartFormDataContent(
                    formData {
                        append(
                            "avatar",
                            avatar,
                            Headers.build {
                                append(HttpHeaders.ContentType, "image/webp")
                                append(HttpHeaders.ContentDisposition, "filename=\"avatar.webp\"")
                            },
                        )
                    },
                ),
            )
        }
    }
}
