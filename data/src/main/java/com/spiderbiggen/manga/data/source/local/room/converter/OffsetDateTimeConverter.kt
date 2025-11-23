package com.spiderbiggen.manga.data.source.local.room.converter

import androidx.room.TypeConverter
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

class OffsetDateTimeConverter {
    @TypeConverter
    fun fromString(value: String?): OffsetDateTime? =
        value?.let { OffsetDateTime.parse(it, DateTimeFormatter.ISO_OFFSET_DATE_TIME) }

    @TypeConverter
    fun toString(value: OffsetDateTime?): String? = value?.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
}
