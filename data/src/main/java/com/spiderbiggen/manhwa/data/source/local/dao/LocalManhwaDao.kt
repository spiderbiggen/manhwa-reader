package com.spiderbiggen.manhwa.data.source.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.spiderbiggen.manhwa.data.source.local.model.LocalManhwaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LocalManhwaDao {
    @Upsert
    suspend fun insert(chapter: List<LocalManhwaEntity>)

    @Query("SELECT * FROM manhwa where id = :id")
    suspend fun get(id: String): LocalManhwaEntity?

    @Query("SELECT * FROM manhwa WHERE source = :source")
    suspend fun getForSource(source: String): List<LocalManhwaEntity>

    @Query("SELECT * FROM manhwa ORDER BY updated_at DESC")
    fun getAll(): Flow<List<LocalManhwaEntity>>
}