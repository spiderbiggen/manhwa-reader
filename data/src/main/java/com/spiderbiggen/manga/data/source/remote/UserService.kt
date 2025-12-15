package com.spiderbiggen.manga.data.source.remote

import com.spiderbiggen.manga.data.source.remote.model.user.FavoriteState
import com.spiderbiggen.manga.data.source.remote.model.user.ReadState
import com.spiderbiggen.manga.data.source.remote.model.user.UserEntity
import com.spiderbiggen.manga.domain.model.id.ChapterId
import com.spiderbiggen.manga.domain.model.id.MangaId
import kotlin.time.Instant

interface UserService {
    suspend fun getSelf(): UserEntity

    suspend fun updateImage(avatar: ByteArray)

    suspend fun getFavorites(since: Instant?): Map<MangaId, FavoriteState>

    suspend fun updateFavorites(updates: Map<MangaId, FavoriteState>): Map<MangaId, FavoriteState>

    suspend fun getReadProgress(since: Instant?): Map<ChapterId, ReadState>

    suspend fun updateReadProgress(updates: Map<ChapterId, ReadState>): Map<ChapterId, ReadState>
}
