package com.spiderbiggen.manhwa.data.source.local.converter

import androidx.room.TypeConverter
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toKotlinLocalDate
import java.time.format.DateTimeFormatter
import java.time.LocalDate as JavaLocalDate

class LocalDateConverter {
    @TypeConverter
    fun fromString(value: String?): LocalDate? =
        value?.let { JavaLocalDate.parse(it, DateTimeFormatter.ISO_LOCAL_DATE).toKotlinLocalDate() }

    @TypeConverter
    fun toString(value: LocalDate?): String? =
        value?.toJavaLocalDate()?.format(DateTimeFormatter.ISO_LOCAL_DATE)
}
