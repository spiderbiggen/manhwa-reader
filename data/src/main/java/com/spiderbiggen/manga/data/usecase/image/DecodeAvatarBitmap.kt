package com.spiderbiggen.manga.data.usecase.image

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import arrow.core.Either
import arrow.core.raise.either
import com.spiderbiggen.manga.data.usecase.appError
import com.spiderbiggen.manga.domain.model.AppError
import java.io.ByteArrayOutputStream
import kotlin.math.roundToInt

class DecodeAvatarBitmap(private val contentResolver: ContentResolver) {
    operator fun invoke(uri: Uri): Either<AppError, Bitmap> = either {
        appError {
            contentResolver.decodeSimpleImage(uri)
        }
    }

    private fun ContentResolver.decodeSimpleImage(uri: Uri): Bitmap {
        val bytes = openInputStream(uri)!!.use { source ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                source.readAllBytes()
            } else {
                ByteArrayOutputStream().use { sink ->
                    source.copyTo(sink)
                    sink.toByteArray()
                }
            }
        }
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)

        options.inJustDecodeBounds = false
        options.inSampleSize = calculateInSampleSize(options)

        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)!!
    }

    private companion object {
        private const val MAX_DIMENSION = 512f

        private fun calculateInSampleSize(options: BitmapFactory.Options): Int {
            val height = options.outHeight
            val width = options.outWidth
            var inSampleSize = 1

            if (height > MAX_DIMENSION || width > MAX_DIMENSION) {
                // Calculate ratios of height and width to requested height and width
                val heightRatio = (height.toFloat() / MAX_DIMENSION).roundToInt()
                val widthRatio = (width.toFloat() / MAX_DIMENSION).roundToInt()

                // Choose the largest ratio as inSampleSize value, this will guarantee
                // a final image with both dimensions smaller than or equal to the
                // maximum height and width.
                inSampleSize = if (heightRatio > widthRatio) heightRatio else widthRatio
            }
            return inSampleSize
        }
    }
}
