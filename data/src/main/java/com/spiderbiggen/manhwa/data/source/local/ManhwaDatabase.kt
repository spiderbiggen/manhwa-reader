package com.spiderbiggen.manhwa.data.source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
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
    version = 3,
    autoMigrations = [
    ],
    exportSchema = true
)
abstract class ManhwaDatabase : RoomDatabase() {
    abstract fun localManhwaDao(): LocalManhwaDao
    abstract fun localChapterDao(): LocalChapterDao

    class Migration1To2 : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            try {
                db.beginTransactionNonExclusive()
                db.execSQL("ALTER TABLE chapter RENAME TO old_chapter")
                db.execSQL(
                    """CREATE TABLE IF NOT EXISTS `chapter` (
                    |    `id` TEXT NOT NULL,
                    |    `manhwa_id` TEXT NOT NULL,
                    |    `number` INTEGER NOT NULL,
                    |    `decimal` INTEGER NOT NULL DEFAULT 0,
                    |    `title` TEXT,
                    |    `date` TEXT NOT NULL,
                    |    `image_chunks` INTEGER NOT NULL DEFAULT 0,
                    |    PRIMARY KEY(`id`),
                    |    FOREIGN KEY(`manhwa_id`) REFERENCES `manhwa`(`id`) ON UPDATE NO ACTION ON DELETE NO ACTION
                    |)""".trimMargin()
                )
                db.execSQL(
                    """
                    |INSERT INTO chapter (id, manhwa_id, number, decimal, title, date, image_chunks)
                    |SELECT id, manhwa_id, number, COALESCE(decimal, 0) decimal, title, date, image_chunks
                    |FROM old_chapter
                    |""".trimMargin()
                )
                db.execSQL("DROP TABLE old_chapter")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_chapter_manhwa_id` ON `chapter` (`manhwa_id`)")
                db.setTransactionSuccessful()
            } finally {
                db.endTransaction()
            }
        }
    }

    class Migration2To3 : Migration(2, 3) {
        override fun migrate(db: SupportSQLiteDatabase) {
            try {
                db.beginTransactionNonExclusive()
                db.execSQL("ALTER TABLE manhwa RENAME TO old_manhwa")
                db.execSQL(
                    """CREATE TABLE IF NOT EXISTS `manhwa` (
                    |    `id` TEXT NOT NULL,
                    |    `source` TEXT NOT NULL,
                    |    `title` TEXT NOT NULL,
                    |    `cover` TEXT NOT NULL,
                    |    `description` TEXT NOT NULL,
                    |    `status` TEXT NOT NULL,
                    |    `updated_at` TEXT NOT NULL,
                    |    PRIMARY KEY(`id`)
                    |)""".trimMargin()
                )
                db.execSQL(
                    """
                    |INSERT INTO manhwa (id, source, title, cover, description, status, updated_at)
                    |SELECT id, source, title, coverImage, description, status, updatedAt
                    |FROM old_manhwa
                    |""".trimMargin()
                )
                db.execSQL("DROP TABLE old_manhwa")
                db.setTransactionSuccessful()
            } finally {
                db.endTransaction()
            }
        }
    }
}