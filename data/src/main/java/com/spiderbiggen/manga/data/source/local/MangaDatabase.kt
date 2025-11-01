package com.spiderbiggen.manga.data.source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.spiderbiggen.manga.data.source.local.converter.InstantConverter
import com.spiderbiggen.manga.data.source.local.converter.LocalDateConverter
import com.spiderbiggen.manga.data.source.local.converter.OffsetDateTimeConverter
import com.spiderbiggen.manga.data.source.local.dao.LocalChapterDao
import com.spiderbiggen.manga.data.source.local.dao.LocalMangaDao
import com.spiderbiggen.manga.data.source.local.model.LocalChapterEntity
import com.spiderbiggen.manga.data.source.local.model.LocalMangaEntity

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
    version = 6,
    autoMigrations = [],
    exportSchema = true,
)
abstract class MangaDatabase : RoomDatabase() {
    abstract fun localMangaDao(): LocalMangaDao
    abstract fun localChapterDao(): LocalChapterDao
}

class MangaDatabaseDecorator(private val database: MangaDatabase) {
    fun localMangaDao() = database.localMangaDao()
    fun localChapterDao() = database.localChapterDao()
}
