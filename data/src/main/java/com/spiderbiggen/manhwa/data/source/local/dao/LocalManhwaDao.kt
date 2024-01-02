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

    @Query("SELECT m.* FROM manhwa m LEFT JOIN chapter c on c.manhwa_id = m.id AND c.updated_at = m.updated_at ORDER BY updated_at DESC")
    fun getAll(): Flow<List<LocalManhwaEntity>>

    @Query(
        """
        SELECT DISTINCT(m.id) FROM manhwa m
        WHERE m.updated_at > (SELECT MAX(updated_at) FROM chapter where manhwa_id = m.id)
        """
    )
    suspend fun getForUpdate(): List<String>
}