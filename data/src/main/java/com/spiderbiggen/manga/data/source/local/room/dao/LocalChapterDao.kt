package com.spiderbiggen.manga.data.source.local.room.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.spiderbiggen.manga.data.source.local.room.model.chapter.LocalChapterEntity
import com.spiderbiggen.manga.data.source.local.room.model.chapter.LocalChapterForOverview
import com.spiderbiggen.manga.domain.model.id.ChapterId
import com.spiderbiggen.manga.domain.model.id.MangaId
import kotlinx.coroutines.flow.Flow

@Dao
interface LocalChapterDao {
    @Upsert
    suspend fun insert(chapter: List<LocalChapterEntity>)

    @Query("SELECT * FROM chapter where id = :id")
    suspend fun get(id: ChapterId): LocalChapterEntity?

    @Query("SELECT * FROM chapter WHERE manga_id = :mangaId ORDER BY index_num DESC, COALESCE(sub_index, 0) DESC")
    fun getFlowForMangaId(mangaId: MangaId): Flow<List<LocalChapterEntity>>

    @Query(
        """
        SELECT *, COALESCE(r.is_read, 0) as is_read
        FROM chapter c
            LEFT JOIN chapter_read_status r ON c.id = r.id
        WHERE c.manga_id = :mangaId 
        ORDER BY index_num DESC, COALESCE(sub_index, 0) DESC
        """,
    )
    fun getFlowForMangaOverview(mangaId: MangaId): Flow<List<LocalChapterForOverview>>

    @Query(
        """
        SELECT *, COALESCE(r.is_read, 0) as is_read
        FROM chapter c
            LEFT JOIN chapter_read_status r ON c.id = r.id
        WHERE c.id = :id
        LIMIT 1
        """,
    )
    fun getFlowForChapterOverview(id: ChapterId): Flow<LocalChapterForOverview?>

    @Query("SELECT * FROM chapter WHERE manga_id = :mangaId ORDER BY index_num DESC, COALESCE(sub_index, 0) DESC")
    suspend fun getForMangaId(mangaId: MangaId): List<LocalChapterEntity>

    @Query("DELETE FROM chapter WHERE manga_id = :mangaId AND id NOT IN (:knownIds)")
    suspend fun removeUnknown(mangaId: MangaId, knownIds: List<ChapterId>)

    @Query(
        """
        SELECT c1.id FROM chapter c1 LEFT JOIN chapter c2 USING (manga_id)
        WHERE c2.id = :id AND c1.index_num < c2.index_num OR (c1.index_num = c2.index_num AND COALESCE(c1.sub_index, 0) < COALESCE(c2.sub_index, 0))
        ORDER BY c1.index_num DESC, COALESCE(c1.sub_index, 0) DESC
        """,
    )
    suspend fun getPreviousChapterIds(id: ChapterId): List<ChapterId>

    @Query(
        """
        SELECT c1.id FROM chapter c1 LEFT JOIN chapter c2 USING (manga_id)
        WHERE c2.id = :id 
            AND (
                c1.index_num < c2.index_num 
                OR (c1.index_num = c2.index_num AND COALESCE(c1.sub_index, 0) < COALESCE(c2.sub_index, 0))
            )
        ORDER BY c1.index_num DESC, COALESCE(c1.sub_index, 0) DESC
        LIMIT 1
        """,
    )
    suspend fun getPrevChapterId(id: ChapterId): ChapterId?

    @Query(
        """
        SELECT c1.id FROM chapter c1 LEFT JOIN chapter c2 USING (manga_id)
        WHERE c2.id = :id 
            AND (
                c1.index_num > c2.index_num 
                OR (c1.index_num = c2.index_num AND COALESCE(c1.sub_index, 0) > COALESCE(c2.sub_index, 0))
            )
        ORDER BY c1.index_num ASC, COALESCE(c1.sub_index, 0) ASC
        LIMIT 1
        """,
    )
    suspend fun getNextChapterId(id: ChapterId): ChapterId?
}
