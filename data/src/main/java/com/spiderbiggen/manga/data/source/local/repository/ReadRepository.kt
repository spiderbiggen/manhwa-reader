package com.spiderbiggen.manga.data.source.local.repository

import android.content.SharedPreferences
import androidx.core.content.edit
import com.spiderbiggen.manga.data.source.local.dao.ChapterReadStatusDao
import com.spiderbiggen.manga.data.source.local.model.chapter.ChapterReadStatusEntity
import com.spiderbiggen.manga.domain.model.id.ChapterId
import javax.inject.Inject
import javax.inject.Provider
import kotlin.time.Clock.System.now
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

class ReadRepository @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    private val readDaoProvider: Provider<ChapterReadStatusDao>,
) {

    private companion object {
        private const val READ_KEY_PREFIX = "read"
    }

    init {
        // This is a temporary solution until the app is migrated to Room.
        runBlocking {
            val oldChapterIds = sharedPreferences.all
                .filterKeys { it.startsWith(READ_KEY_PREFIX) }
                .mapNotNull { (key, value) ->
                    val id = ChapterId(key.substringAfter('_'))
                    if (value as Boolean) id else null
                }
                .toSet()

            if (oldChapterIds.isEmpty()) return@runBlocking
            val date = now()
            readDaoProvider.get().insert(
                oldChapterIds.map {
                    ChapterReadStatusEntity(
                        id = it,
                        isRead = true,
                        updatedAt = date,
                    )
                },
            )

            sharedPreferences.edit {
                oldChapterIds.forEach {
                    remove("${READ_KEY_PREFIX}_${it.inner}")
                }
            }
        }
    }

    fun getFlow(id: ChapterId): Result<Flow<Boolean?>> = runCatching {
        readDaoProvider.get().isReadFlow(id)
    }

    suspend fun get(id: ChapterId): Result<Boolean> = runCatching {
        readDaoProvider.get().isRead(id) == true
    }

    suspend fun set(id: ChapterId, isRead: Boolean): Result<Unit> = runCatching {
        readDaoProvider.get().insert(ChapterReadStatusEntity(id, isRead))
    }

    suspend fun setReadForPreviousChapters(id: ChapterId): Result<Unit> = runCatching {
        // TODO
    }
}
