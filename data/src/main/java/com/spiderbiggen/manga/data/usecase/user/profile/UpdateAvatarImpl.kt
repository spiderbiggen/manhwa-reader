package com.spiderbiggen.manga.data.usecase.user.profile

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import coil3.ImageLoader
import coil3.memory.MemoryCache
import coil3.request.ImageRequest
import com.spiderbiggen.manga.data.source.remote.ProfileService
import com.spiderbiggen.manga.data.source.remote.usecase.FetchCurrentUser
import com.spiderbiggen.manga.data.usecase.auth.RefreshAccessToken
import com.spiderbiggen.manga.data.usecase.either
import com.spiderbiggen.manga.data.usecase.image.DecodeAvatarBitmap
import com.spiderbiggen.manga.data.usecase.image.EncodeBitmap
import com.spiderbiggen.manga.domain.model.AppError
import com.spiderbiggen.manga.domain.model.Either
import com.spiderbiggen.manga.domain.model.andThenLeft
import com.spiderbiggen.manga.domain.model.mapLeft
import com.spiderbiggen.manga.domain.usecase.user.GetUser
import com.spiderbiggen.manga.domain.usecase.user.profile.UpdateAvatar
import dagger.hilt.android.qualifiers.ApplicationContext
import java.net.URI
import javax.inject.Inject
import javax.inject.Provider
import kotlinx.coroutines.flow.firstOrNull

class UpdateAvatarImpl @Inject constructor(
    @ApplicationContext private val context: Provider<Context>,
    private val imageLoader: Provider<ImageLoader>,
    private val profileService: Provider<ProfileService>,
    private val getUser: GetUser,
    private val refreshAccessToken: RefreshAccessToken,
    private val fetchCurrentUser: FetchCurrentUser,
    // Image manipulation
    private val resizeBitmap: DecodeAvatarBitmap,
    private val encodeBitmap: EncodeBitmap,
) : UpdateAvatar {
    override suspend fun invoke(avatar: URI): Either<Unit, AppError> = refreshAccessToken()
        .andThenLeft { processBitmap(avatar) }
        .andThenLeft { uploadAvatar(it) }
        .andThenLeft { invalidateAvatarCache() }
        .andThenLeft { fetchCurrentUser() }
        .mapLeft {}

    private fun processBitmap(avatar: URI) = runCatching {
        val uri = avatar.toString().toUri()
        val bitmap = resizeBitmap(uri).getOrThrow()
        encodeBitmap(bitmap).getOrThrow()
    }.either()

    private suspend fun uploadAvatar(avatar: ByteArray) = runCatching {
        profileService.get().updateImage(avatar = avatar)
    }.either()

    private suspend fun invalidateAvatarCache() = runCatching {
        // This should never be null here
        getUser().firstOrNull()?.avatarUrl?.let { avatarUrl ->
            with(imageLoader.get()) {
                diskCache?.remove(avatarUrl)
                memoryCache?.remove(MemoryCache.Key(avatarUrl))
                enqueue(ImageRequest.Builder(context.get()).data(avatarUrl).build())
            }
        }
        Unit
    }.either()
}
