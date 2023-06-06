package com.spiderbiggen.manhwa.data.source.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.spiderbiggen.manhwa.data.source.local.model.LocalChapterEntity

@Dao
interface LocalChapterDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(chapter: LocalChapterEntity)

    @Query("SELECT * FROM chapter WHERE manhwaId = :manhwaId")
    fun getForManhwaId(manhwaId: String): LocalChapterEntity
}