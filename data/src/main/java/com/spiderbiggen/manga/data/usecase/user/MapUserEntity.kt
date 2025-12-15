package com.spiderbiggen.manga.data.usecase.user

import com.spiderbiggen.manga.data.di.BaseUrl
import com.spiderbiggen.manga.data.source.remote.model.user.UserEntity
import com.spiderbiggen.manga.domain.model.auth.User
import javax.inject.Inject

class MapUserEntity @Inject constructor(@BaseUrl private val baseUrl: String) {
    operator fun invoke(entity: UserEntity): User {
        val simplifiedId = entity.id.filter { it.isLetterOrDigit() }
        return User(
            id = entity.id,
            username = entity.username,
            avatarUrl = "$baseUrl/api/v1/users/$simplifiedId/avatar",
            email = entity.email,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
        )
    }

    operator fun invoke(entity: User): UserEntity = UserEntity(
        id = entity.id,
        username = entity.username,
        email = entity.email,
        createdAt = entity.createdAt,
        updatedAt = entity.updatedAt,
    )
}
