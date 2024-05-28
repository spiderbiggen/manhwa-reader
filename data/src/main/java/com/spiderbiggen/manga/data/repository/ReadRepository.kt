package com.spiderbiggen.manga.data.repository

import android.content.SharedPreferences
import androidx.core.content.edit
import com.spiderbiggen.manga.domain.model.id.ChapterId
import javax.inject.Inject

class ReadRepository @Inject constructor(
    private val sharedPreferences: SharedPreferences,
) {

    private companion object {
        private const val READ_KEY_PREFIX = "read"
    }

    fun isRead(id: ChapterId) =
        sharedPreferences.getBoolean("${READ_KEY_PREFIX}_${id.inner}", false)

    fun setRead(id: ChapterId, isRead: Boolean) {
        sharedPreferences.edit {
            putBoolean("${READ_KEY_PREFIX}_${id.inner}", isRead)
        }
    }
}
