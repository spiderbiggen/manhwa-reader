package com.spiderbiggen.manga.presentation.image

import android.content.Context
import androidx.compose.runtime.Immutable
import coil3.ImageLoader
import coil3.disk.DiskCache
import java.io.File
import okio.Path.Companion.toOkioPath

/**
 * Image loader dedicated to chapter pages.
 *
 * Chapter pages are large and numerous, so they get their own disk and memory caches. Otherwise
 * they evict the small, long-lived images (covers, avatars) held by the default Coil loader.
 */
@Immutable class ChapterImageLoader(val imageLoader: ImageLoader)

internal const val CHAPTER_IMAGE_CACHE_DIRECTORY = "chapter_image_cache"

internal fun chapterImageCacheDirectory(cacheDir: File): File =
    File(cacheDir, CHAPTER_IMAGE_CACHE_DIRECTORY)

fun createChapterImageLoader(context: Context): ChapterImageLoader {
    val appContext = context.applicationContext
    val loader =
        ImageLoader.Builder(appContext)
            .diskCache {
                DiskCache.Builder()
                    .directory(chapterImageCacheDirectory(appContext.cacheDir).toOkioPath())
                    .build()
            }
            .build()
    return ChapterImageLoader(loader)
}
