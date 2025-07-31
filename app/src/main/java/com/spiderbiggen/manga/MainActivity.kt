package com.spiderbiggen.manga

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat
import coil3.SingletonImageLoader
import coil3.disk.DiskCache
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import com.google.firebase.Firebase
import com.spiderbiggen.manga.presentation.ui.main.MainContent
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import okio.Path.Companion.toOkioPath

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var analytics: FirebaseAnalytics

    private val coverDiskCache: DiskCache
        get() = DiskCache.Builder().directory(File(cacheDir, "covers").toOkioPath()).build()
    private val chapterDiskCache: DiskCache
        get() = DiskCache.Builder().directory(File(cacheDir, "chapters").toOkioPath()).build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Obtain the FirebaseAnalytics instance.
        analytics = Firebase.analytics
        WindowCompat.setDecorFitsSystemWindows(window, false)

        val coverImageLoader = SingletonImageLoader.get(this).newBuilder()
            .diskCache(coverDiskCache).build()
        val chapterImageLoader = SingletonImageLoader.get(this).newBuilder()
            .diskCache(chapterDiskCache).build()

        enableEdgeToEdge()
        setContent {
            MainContent(
                coverImageLoader = coverImageLoader,
                chapterImageLoader = chapterImageLoader,
            )
        }
    }
}
