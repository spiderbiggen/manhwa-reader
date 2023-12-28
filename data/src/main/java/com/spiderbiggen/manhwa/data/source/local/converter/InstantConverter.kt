package com.spiderbiggen.manhwa.data.source.local.converter

import androidx.room.TypeConverter
import kotlinx.datetime.Instant
import kotlinx.datetime.toKotlinInstant
import java.time.Instant as JavaInstant

class InstantConverter {
    @TypeConverter
    fun fromString(value: String?): Instant? =
        value?.let { JavaInstant.parse(it).toKotlinInstant() }

    @TypeConverter
    fun toString(value: Instant?): String? =
        value?.toString()
}
