package com.spiderbiggen.manhwa.data.source.local

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.DeleteTable
import androidx.room.RenameColumn
import androidx.room.RenameTable
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.AutoMigrationSpec
import com.spiderbiggen.manhwa.data.source.local.converter.InstantConverter
import com.spiderbiggen.manhwa.data.source.local.converter.LocalDateConverter
import com.spiderbiggen.manhwa.data.source.local.converter.OffsetDateTimeConverter
import com.spiderbiggen.manhwa.data.source.local.dao.LocalChapterDao
import com.spiderbiggen.manhwa.data.source.local.dao.LocalMangaDao
import com.spiderbiggen.manhwa.data.source.local.model.LocalChapterEntity
import com.spiderbiggen.manhwa.data.source.local.model.LocalMangaEntity

@TypeConverters(
    InstantConverter::class,
    LocalDateConverter::class,
    OffsetDateTimeConverter::class,
)
@Database(
    entities = [
        LocalChapterEntity::class,
        LocalMangaEntity::class,
    ],
    version = 4,
    autoMigrations = [],
    exportSchema = true,
)
abstract class MangaDatabase : RoomDatabase() {
    abstract fun localMangaDao(): LocalMangaDao
    abstract fun localChapterDao(): LocalChapterDao
}
