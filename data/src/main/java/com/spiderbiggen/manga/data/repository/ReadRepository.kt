package com.spiderbiggen.manga.data.repository

import android.content.SharedPreferences
import androidx.core.content.edit
import javax.inject.Inject

class ReadRepository @Inject constructor(
    private val sharedPreferences: SharedPreferences,
) {

    private companion object {
        private const val READ_KEY_PREFIX = "read"
    }

    fun isRead(chapterId: String) =
        sharedPreferences.getBoolean("${READ_KEY_PREFIX}_$chapterId", false)

    fun setRead(chapterId: String, isRead: Boolean) {
        sharedPreferences.edit {
            putBoolean("${READ_KEY_PREFIX}_$chapterId", isRead)
        }
    }
}
