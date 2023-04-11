package com.spiderbiggen.manhwa.glide

import android.content.Context
import android.util.Log
import com.bumptech.glide.load.Key
import com.bumptech.glide.load.engine.cache.DiskCache
import com.bumptech.glide.load.engine.cache.DiskLruCacheWrapper
import com.spiderbiggen.manhwa.presentation.glide.ItemSignature
import com.spiderbiggen.manhwa.presentation.glide.ItemType
import java.io.File
import java.lang.reflect.Field


class ManhwaDiskCache(coverDir: File, chapterDir: File) : DiskCache {
    private companion object {
        private const val TAG = "ManhwaDiskCache"

        private const val RESOURCE_KEY_CLASS = "com.bumptech.glide.load.engine.ResourceCacheKey"
        private const val DATA_KEY_CLASS = "com.bumptech.glide.load.engine.DataCacheKey"

        private const val COVER_CACHE_SIZE: Long = 1024 * 1024 * 25
        private const val CHAPTER_CACHE_SIZE: Long = 1024 * 1024 * 100

        private lateinit var signatureInResourceCacheKey: Field
        private lateinit var signatureInDataCacheKey: Field

        init {
            try {
                signatureInResourceCacheKey =
                    Class.forName(RESOURCE_KEY_CLASS).getDeclaredField("signature").apply {
                        isAccessible = true
                    }
                signatureInDataCacheKey =
                    Class.forName(DATA_KEY_CLASS).getDeclaredField("signature").apply {
                        isAccessible = true
                    }
            } catch (e: ClassNotFoundException) {
                Log.w(TAG, "find ResourceCacheKey failed", e)
            } catch (e: NoSuchFieldException) {
                Log.w(TAG, "reflect signature failed", e)
            } catch (error: Error) {
                Log.w(TAG, "reflect signature failed", error)
            }
        }
    }

    private val coverCache: DiskCache = DiskLruCacheWrapper.create(coverDir, COVER_CACHE_SIZE)
    private val chapterCache: DiskCache = DiskLruCacheWrapper.create(chapterDir, CHAPTER_CACHE_SIZE)

    override fun get(key: Key): File? = getCacheForKey(key).get(key)

    override fun put(key: Key, writer: DiskCache.Writer?) = getCacheForKey(key).put(key, writer)

    override fun delete(key: Key) = getCacheForKey(key).delete(key)

    override fun clear() {
        coverCache.clear()
        chapterCache.clear()
    }

    private fun getCacheForKey(key: Key): DiskCache {
        val signature = runCatching { signatureInDataCacheKey.get(key) }.getOrNull()
            ?: runCatching { signatureInResourceCacheKey.get(key) }.getOrNull()
        return when (signature) {
            is ItemSignature -> when (signature.type) {
                ItemType.COVER -> coverCache
                ItemType.CHAPTER -> chapterCache
            }

            else -> chapterCache
        }
    }

    class Factory(
        private val context: Context,
        private val baseDir: String = DiskCache.Factory.DEFAULT_DISK_CACHE_DIR
    ) : DiskCache.Factory {
        private companion object {
            private const val COVER_CACHE_DIR = "covers"
            private const val CHAPTER_CACHE_DIR = "chapters"
        }

        override fun build(): DiskCache? {
            val cacheDir = File(context.cacheDir, baseDir)
            val coverDir = File(cacheDir, COVER_CACHE_DIR)
            if (!coverDir.exists() && !coverDir.mkdirs()) {
                return null
            }
            val chapterDir = File(cacheDir, CHAPTER_CACHE_DIR)
            if (!chapterDir.exists() && !chapterDir.mkdirs()) {
                return null
            }
            return ManhwaDiskCache(coverDir, chapterDir)
        }

    }
}