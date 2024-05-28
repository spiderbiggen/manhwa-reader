package com.spiderbiggen.manga.domain.model.id

import kotlinx.serialization.Serializable


@JvmInline
@Serializable
value class MangaId(val inner: String)
