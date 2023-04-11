package com.spiderbiggen.manhwa.presentation.glide

import com.bumptech.glide.load.model.GlideUrl

data class ItemSignature(
    val type: ItemType,
    val key: String,
) : GlideUrl(key)
