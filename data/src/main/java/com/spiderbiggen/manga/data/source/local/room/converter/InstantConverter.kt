package com.spiderbiggen.manga.data.source.local.room.converter

import androidx.room.TypeConverter
import java.time.Instant as JavaInstant
import kotlin.time.Instant
import kotlin.time.toKotlinInstant

class InstantConverter {
    @TypeConverter
    fun fromString(value: String?): Instant? = value?.let { JavaInstant.parse(it).toKotlinInstant() }

    @TypeConverter
    fun toString(value: Instant?): String? = value?.toString()
}
