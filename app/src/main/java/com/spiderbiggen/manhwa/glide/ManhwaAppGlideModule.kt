package com.spiderbiggen.manhwa.glide

import android.content.Context
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule


@GlideModule
class ManhwaAppGlideModule : AppGlideModule() {
    override fun isManifestParsingEnabled(): Boolean = false

    override fun applyOptions(context: Context, builder: GlideBuilder) {
        builder.setDiskCache(ManhwaDiskCache.Factory(context))
    }
}