package com.spiderbiggen.manhwa.data.source.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.spiderbiggen.manhwa.data.source.local.model.LocalChapterEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LocalChapterDao {
    @Upsert
    suspend fun insert(chapter: List<LocalChapterEntity>)

    @Query("SELECT * FROM chapter where id = :id")
    suspend fun get(id: String): LocalChapterEntity?

    @Query("SELECT * FROM chapter WHERE manhwa_id = :manhwaId ORDER BY number DESC, decimal DESC")
    fun getFlowForManhwaId(manhwaId: String): Flow<List<LocalChapterEntity>>

    @Query("SELECT * FROM chapter WHERE manhwa_id = :manhwaId ORDER BY number DESC, decimal DESC")
    suspend fun getForManhwaId(manhwaId: String): List<LocalChapterEntity>

    @Query(
        """
        SELECT c1.* FROM chapter c1 LEFT JOIN chapter c2 USING (manhwa_id)
        WHERE c2.id = :id 
        AND (
            c1.number < c2.number 
            OR (c1.number = c2.number AND c1.decimal < c2.decimal) 
        )
        ORDER BY c1.number DESC, c1.decimal DESC
        """
    )
    suspend fun getPreviousChapters(id: String): List<LocalChapterEntity>

    @Query(
        """
        SELECT c1.* FROM chapter c1 LEFT JOIN chapter c2 USING (manhwa_id)
        WHERE c2.id = :id 
        AND (
            c1.number < c2.number 
            OR (c1.number = c2.number AND c1.decimal < c2.decimal) 
        )
        ORDER BY c1.number DESC, c1.decimal DESC
        LIMIT 1
        """
    )
    suspend fun getPrevChapterId(id: String): LocalChapterEntity?

    @Query(
        """
        SELECT c1.* FROM chapter c1 LEFT JOIN chapter c2 USING (manhwa_id)
        WHERE c2.id = :id 
        AND (
            c1.number > c2.number 
            OR (c1.number = c2.number AND c1.decimal > c2.decimal)
        )
        ORDER BY c1.number ASC, c1.decimal ASC
        LIMIT 1
        """
    )
    suspend fun getNextChapterId(id: String): LocalChapterEntity?
}