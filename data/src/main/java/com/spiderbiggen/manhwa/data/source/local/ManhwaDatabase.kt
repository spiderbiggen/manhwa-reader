package com.spiderbiggen.manhwa.data.source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.spiderbiggen.manhwa.data.source.local.converter.LocalDateConverter
import com.spiderbiggen.manhwa.data.source.local.converter.OffsetDateTimeConverter
import com.spiderbiggen.manhwa.data.source.local.dao.LocalChapterDao
import com.spiderbiggen.manhwa.data.source.local.dao.LocalManhwaDao
import com.spiderbiggen.manhwa.data.source.local.model.LocalChapterEntity
import com.spiderbiggen.manhwa.data.source.local.model.LocalManhwaEntity

@TypeConverters(
    OffsetDateTimeConverter::class,
    LocalDateConverter::class,
)
@Database(
    entities = [
        LocalManhwaEntity::class,
        LocalChapterEntity::class
    ],
    version = 1,
    autoMigrations = [
    ],
    exportSchema = true
)
abstract class ManhwaDatabase : RoomDatabase() {
    abstract fun localManhwaDao(): LocalManhwaDao
    abstract fun localChapterDao(): LocalChapterDao
}