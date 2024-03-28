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

    @Query("SELECT * FROM chapter WHERE manga_id = :mangaId ORDER BY number DESC")
    fun getFlowForMangaId(mangaId: String): Flow<List<LocalChapterEntity>>

    @Query("SELECT * FROM chapter WHERE manga_id = :mangaId ORDER BY number DESC")
    suspend fun getForMangaId(mangaId: String): List<LocalChapterEntity>

    @Query("DELETE FROM chapter where manga_id = :mangaId AND id NOT IN (:knownIds)")
    suspend fun removeUnknown(mangaId: String, knownIds: List<String>)

    @Query(
        """
        SELECT c1.* FROM chapter c1 LEFT JOIN chapter c2 USING (manga_id)
        WHERE c2.id = :id AND c1.number < c2.number
        ORDER BY c1.number DESC
        """,
    )
    suspend fun getPreviousChapters(id: String): List<LocalChapterEntity>

    @Query(
        """
        SELECT c1.* FROM chapter c1 LEFT JOIN chapter c2 USING (manga_id)
        WHERE c2.id = :id AND c1.number < c2.number
        ORDER BY c1.number DESC
        LIMIT 1
        """,
    )
    suspend fun getPrevChapterId(id: String): LocalChapterEntity?

    @Query(
        """
        SELECT c1.* FROM chapter c1 LEFT JOIN chapter c2 USING (manga_id)
        WHERE c2.id = :id AND c1.number > c2.number
        ORDER BY c1.number ASC
        LIMIT 1
        """,
    )
    suspend fun getNextChapterId(id: String): LocalChapterEntity?
}
