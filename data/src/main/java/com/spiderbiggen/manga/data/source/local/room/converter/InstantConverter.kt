package com.spiderbiggen.manga.data.source.local.room.converter

import androidx.room3.ColumnTypeConverter
import java.time.Instant as JavaInstant
import kotlin.time.Instant
import kotlin.time.toKotlinInstant

class InstantConverter {
    @ColumnTypeConverter
    fun fromString(value: String?): Instant? = value?.let {
        JavaInstant.parse(it).toKotlinInstant()
    }

    @ColumnTypeConverter fun toString(value: Instant?): String? = value?.toString()
}
