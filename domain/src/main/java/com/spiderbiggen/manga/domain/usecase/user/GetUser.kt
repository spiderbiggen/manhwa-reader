package com.spiderbiggen.manga.domain.usecase.user

import com.spiderbiggen.manga.domain.model.auth.User
import kotlinx.coroutines.flow.Flow

interface GetUser {
    operator fun invoke(): Flow<User?>
}
