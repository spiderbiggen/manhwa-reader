package com.spiderbiggen.manhwa

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat
import com.spiderbiggen.manhwa.domain.usecase.remote.StartRemoteUpdate
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

        enableEdgeToEdge()
        setContent {
            MainContent()
        }
    }

    override fun onResume() {
        super.onResume()
        startRemoteUpdate.get().invoke(skipCache = false)
    }
}
