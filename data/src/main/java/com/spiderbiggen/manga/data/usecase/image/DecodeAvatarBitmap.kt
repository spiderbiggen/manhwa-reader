package com.spiderbiggen.manga.data.usecase.image

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Provider
import kotlin.math.roundToInt

class DecodeAvatarBitmap @Inject constructor(
    @ApplicationContext private val context: Provider<Context>,
) {
    operator fun invoke(uri: Uri): Result<Bitmap> = runCatching {
        context.get().contentResolver.decodeSimpleImage(uri)
    }

    private fun ContentResolver.decodeSimpleImage(uri: Uri): Bitmap {
        val bytes = openInputStream(uri)!!.use { source ->
            source.readAllBytes()
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
