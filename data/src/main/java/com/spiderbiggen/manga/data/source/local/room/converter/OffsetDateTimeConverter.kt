package com.spiderbiggen.manga.data.source.local.room.converter

import androidx.room3.ColumnTypeConverter
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

class OffsetDateTimeConverter {
    @ColumnTypeConverter
    fun fromString(value: String?): OffsetDateTime? = value?.let {
        OffsetDateTime.parse(it, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
    }

    @ColumnTypeConverter
    fun toString(value: OffsetDateTime?): String? =
        value?.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
}
