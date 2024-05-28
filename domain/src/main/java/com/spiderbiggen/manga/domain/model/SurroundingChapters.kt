package com.spiderbiggen.manga.domain.model

import com.spiderbiggen.manga.domain.model.id.ChapterId

data class SurroundingChapters(val previous: ChapterId? = null, val next: ChapterId? = null)
