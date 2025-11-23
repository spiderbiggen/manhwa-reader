package com.spiderbiggen.manga.data.usecase.image

import android.graphics.Bitmap
import android.os.Build
import java.io.ByteArrayOutputStream
import javax.inject.Inject

class EncodeBitmap @Inject constructor() {
    operator fun invoke(bitmap: Bitmap): Result<ByteArray> = runCatching {
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
