package com.spiderbiggen.manga.data.source.local

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.spiderbiggen.manga.data.source.local.converter.InstantConverter
import com.spiderbiggen.manga.data.source.local.converter.LocalDateConverter
import com.spiderbiggen.manga.data.source.local.converter.OffsetDateTimeConverter
import com.spiderbiggen.manga.data.source.local.dao.ChapterReadStatusDao
import com.spiderbiggen.manga.data.source.local.dao.LocalChapterDao
import com.spiderbiggen.manga.data.source.local.dao.LocalMangaDao
import com.spiderbiggen.manga.data.source.local.dao.MangaFavoriteStatusDao
import com.spiderbiggen.manga.data.source.local.model.chapter.ChapterReadStatusEntity
import com.spiderbiggen.manga.data.source.local.model.chapter.LocalChapterEntity
import com.spiderbiggen.manga.data.source.local.model.manga.LocalMangaEntity
import com.spiderbiggen.manga.data.source.local.model.manga.MangaFavoriteStatusEntity

@TypeConverters(
    InstantConverter::class,
    LocalDateConverter::class,
    OffsetDateTimeConverter::class,
)
@Database(
    entities = [
        LocalChapterEntity::class,
        LocalMangaEntity::class,
        MangaFavoriteStatusEntity::class,
        ChapterReadStatusEntity::class,
    ],
    version = 7,
    autoMigrations = [
        AutoMigration(from = 6, to = 7),
    ],
    exportSchema = true,
)
abstract class MangaDatabase : RoomDatabase() {
    abstract fun localMangaDao(): LocalMangaDao
    abstract fun localChapterDao(): LocalChapterDao

    abstract fun mangaFavoriteStatusDao(): MangaFavoriteStatusDao
    abstract fun chapterReadStatusDao(): ChapterReadStatusDao
}

class MangaDatabaseDecorator(private val database: MangaDatabase) {
    fun localMangaDao() = database.localMangaDao()
    fun localChapterDao() = database.localChapterDao()

    fun mangaFavoriteStatusDao() = database.mangaFavoriteStatusDao()
    fun chapterReadStatusDao() = database.chapterReadStatusDao()
}
