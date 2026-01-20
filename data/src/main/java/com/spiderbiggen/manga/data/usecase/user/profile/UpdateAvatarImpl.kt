package com.spiderbiggen.manga.data.usecase.user.profile

import android.content.Context
import androidx.core.net.toUri
import coil3.ImageLoader
import coil3.memory.MemoryCache
import coil3.request.ImageRequest
import com.spiderbiggen.manga.data.source.remote.UserService
import com.spiderbiggen.manga.data.source.remote.usecase.FetchCurrentUser
import com.spiderbiggen.manga.data.usecase.auth.RefreshAccessToken
import com.spiderbiggen.manga.data.usecase.either
import com.spiderbiggen.manga.data.usecase.image.DecodeAvatarBitmap
import com.spiderbiggen.manga.data.usecase.image.EncodeBitmap
import com.spiderbiggen.manga.domain.model.AppError
import arrow.core.Either
import arrow.core.flatMap
import com.spiderbiggen.manga.domain.usecase.user.GetUser
import com.spiderbiggen.manga.domain.usecase.user.profile.UpdateAvatar
import java.net.URI
import kotlinx.coroutines.flow.firstOrNull

class UpdateAvatarImpl(
    private val context: Context,
    private val imageLoader: ImageLoader,
    private val userService: UserService,
    private val getUser: GetUser,
    private val refreshAccessToken: RefreshAccessToken,
    private val fetchCurrentUser: FetchCurrentUser,
    // Image manipulation
    private val resizeBitmap: DecodeAvatarBitmap,
    private val encodeBitmap: EncodeBitmap,
) : UpdateAvatar {
    override suspend fun invoke(avatar: URI): Either<AppError, Unit> = refreshAccessToken()
        .flatMap { processBitmap(avatar) }
        .flatMap { uploadAvatar(it) }
        .flatMap { invalidateAvatarCache() }
        .flatMap { fetchCurrentUser() }
        .map {}

    private fun processBitmap(avatar: URI): Either<AppError, ByteArray> = runCatching {
        val uri = avatar.toString().toUri()
        val bitmap = resizeBitmap(uri).getOrThrow()
        encodeBitmap(bitmap).getOrThrow()
    }.either()

    private suspend fun uploadAvatar(avatar: ByteArray): Either<AppError, Unit> = runCatching {
        userService.updateImage(avatar = avatar)
    }.either()

    private suspend fun invalidateAvatarCache(): Either<AppError, Unit> = runCatching {
        // This should never be null here
        getUser().firstOrNull()?.avatarUrl?.let { avatarUrl ->
            with(imageLoader) {
                diskCache?.remove(avatarUrl)
                memoryCache?.remove(MemoryCache.Key(avatarUrl))
                enqueue(ImageRequest.Builder(context).data(avatarUrl).build())
            }
        }
        Unit
    }.either()
}
