package com.spiderbiggen.manga.data.source.local.converter

import androidx.room.TypeConverter
import java.time.Instant as JavaInstant
import kotlinx.datetime.Instant
import kotlinx.datetime.toKotlinInstant

class InstantConverter {
    @TypeConverter
    fun fromString(value: String?): Instant? = value?.let { JavaInstant.parse(it).toKotlinInstant() }

    @TypeConverter
    fun toString(value: Instant?): String? = value?.toString()
}
