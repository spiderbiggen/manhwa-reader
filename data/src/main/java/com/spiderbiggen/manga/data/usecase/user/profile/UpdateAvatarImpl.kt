package com.spiderbiggen.manga.data.usecase.user.profile

import android.content.Context
import androidx.core.net.toUri
import arrow.core.Either
import arrow.core.raise.either
import coil3.ImageLoader
import coil3.memory.MemoryCache
import coil3.request.ImageRequest
import com.spiderbiggen.manga.data.source.remote.UserService
import com.spiderbiggen.manga.data.source.remote.usecase.FetchCurrentUser
import com.spiderbiggen.manga.data.usecase.appError
import com.spiderbiggen.manga.data.usecase.auth.RefreshAccessToken
import com.spiderbiggen.manga.data.usecase.image.DecodeAvatarBitmap
import com.spiderbiggen.manga.data.usecase.image.EncodeBitmap
import com.spiderbiggen.manga.domain.model.AppError
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
    override suspend fun invoke(avatar: URI): Either<AppError, Unit> = either {
        refreshAccessToken().bind()
        val processedAvatar = processBitmap(avatar).bind()
        uploadAvatar(processedAvatar).bind()
        invalidateAvatarCache().bind()
        fetchCurrentUser().bind()
        Unit
    }

    private suspend fun processBitmap(avatar: URI): Either<AppError, ByteArray> = either {
        val uri = avatar.toString().toUri()
        val bitmap = resizeBitmap(uri).bind()
        encodeBitmap(bitmap).bind()
    }

    private suspend fun uploadAvatar(avatar: ByteArray): Either<AppError, Unit> = either {
        appError {
            userService.updateImage(avatar = avatar)
        }
    }

    private suspend fun invalidateAvatarCache(): Either<AppError, Unit> = either {
        appError {
            // This should never be null here
            getUser().firstOrNull()?.avatarUrl?.let { avatarUrl ->
                with(imageLoader) {
                    diskCache?.remove(avatarUrl)
                    memoryCache?.remove(MemoryCache.Key(avatarUrl))
                    enqueue(ImageRequest.Builder(context).data(avatarUrl).build())
                }
            }
            Unit
        }
    }
}
