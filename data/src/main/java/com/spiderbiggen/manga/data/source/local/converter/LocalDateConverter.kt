package com.spiderbiggen.manga.data.source.local.converter

import androidx.room.TypeConverter
import java.time.LocalDate as JavaLocalDate
import java.time.format.DateTimeFormatter
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toKotlinLocalDate

class LocalDateConverter {
    @TypeConverter
    fun fromString(value: String?): LocalDate? =
        value?.let { JavaLocalDate.parse(it, DateTimeFormatter.ISO_LOCAL_DATE).toKotlinLocalDate() }

    @TypeConverter
    fun toString(value: LocalDate?): String? =
        value?.toJavaLocalDate()?.format(DateTimeFormatter.ISO_LOCAL_DATE)
}
