package com.spiderbiggen.manga.data.usecase.image

import android.graphics.Bitmap
import android.os.Build
import arrow.core.Either
import arrow.core.raise.either
import com.spiderbiggen.manga.data.usecase.appError
import com.spiderbiggen.manga.domain.model.AppError
import java.io.ByteArrayOutputStream

class EncodeBitmap {
    operator fun invoke(bitmap: Bitmap): Either<AppError, ByteArray> = either {
        appError {
            ByteArrayOutputStream().use { stream ->
                @Suppress("DEPRECATION")
                val format = when {
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> Bitmap.CompressFormat.WEBP_LOSSY
                    else -> Bitmap.CompressFormat.WEBP
                }
                bitmap.compress(format, 85, stream)
                stream.toByteArray()
            }
        }
    }
}
