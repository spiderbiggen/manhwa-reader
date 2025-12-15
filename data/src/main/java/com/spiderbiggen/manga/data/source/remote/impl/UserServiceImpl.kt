package com.spiderbiggen.manga.data.source.remote.impl

import com.spiderbiggen.manga.data.source.remote.UserService
import com.spiderbiggen.manga.data.source.remote.model.user.FavoriteState
import com.spiderbiggen.manga.data.source.remote.model.user.ReadState
import com.spiderbiggen.manga.data.source.remote.model.user.UserEntity
import com.spiderbiggen.manga.domain.model.id.ChapterId
import com.spiderbiggen.manga.domain.model.id.MangaId
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.compression.compress
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import javax.inject.Inject
import kotlin.time.Instant

class UserServiceImpl @Inject constructor(private val client: HttpClient) : UserService {

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

    override suspend fun getFavorites(since: Instant?): Map<MangaId, FavoriteState> =
        client.get("api/v1/users/favorites") {
            parameter("since", since)
        }.body()

    override suspend fun updateFavorites(updates: Map<MangaId, FavoriteState>): Map<MangaId, FavoriteState> =
        client.post("api/v1/users/favorites") {
            compress("gzip")
            setBody(updates)
        }.body()

    override suspend fun getReadProgress(since: Instant?): Map<ChapterId, ReadState> =
        client.get("api/v1/users/reads") {
            parameter("since", since)
        }.body()

    override suspend fun updateReadProgress(updates: Map<ChapterId, ReadState>): Map<ChapterId, ReadState> =
        client.post("api/v1/users/reads") {
            compress("gzip")
            setBody(updates)
        }.body()
}
