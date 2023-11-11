package com.spiderbiggen.manhwa

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import com.spiderbiggen.manhwa.domain.usecase.StartRemoteUpdate
import com.spiderbiggen.manhwa.presentation.ui.main.MainContent
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import javax.inject.Provider

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var startRemoteUpdate: Provider<StartRemoteUpdate>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            MainContent()
        }
    }

    override fun onPostResume() {
        super.onPostResume()
        startRemoteUpdate.get().invoke()
    }
}
