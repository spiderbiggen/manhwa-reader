package com.spiderbiggen.manga.presentation.ui.main

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.dropUnlessStarted
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import coil3.ImageLoader
import com.spiderbiggen.manga.presentation.components.TrackNavigationSideEffect
import com.spiderbiggen.manga.presentation.theme.MangaReaderTheme
import com.spiderbiggen.manga.presentation.ui.manga.chapter.overview.ChapterOverview
import com.spiderbiggen.manga.presentation.ui.manga.chapter.overview.ChapterViewModel
import com.spiderbiggen.manga.presentation.ui.manga.model.MangaRoutes
import com.spiderbiggen.manga.presentation.ui.manga.overview.MangaOverview
import com.spiderbiggen.manga.presentation.ui.manga.reader.ReadChapterScreen
import com.spiderbiggen.manga.presentation.ui.profile.login.LoginScreen
import com.spiderbiggen.manga.presentation.ui.profile.overview.ProfileOverview
import com.spiderbiggen.manga.presentation.ui.profile.registration.RegistrationScreen
import com.spiderbiggen.manga.presentation.ui.profile.state.ProfileState
import com.spiderbiggen.manga.presentation.ui.profile.state.ProfileViewModel

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MainContent(coverImageLoader: ImageLoader, chapterImageLoader: ImageLoader) {
    val profileViewModel: ProfileViewModel = hiltViewModel()
    val profileState by profileViewModel.state.collectAsStateWithLifecycle()

    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }

    TrackNavigationSideEffect(navController)
    MangaReaderTheme {
        MangaNavHost(
            coverImageLoader = coverImageLoader,
            chapterImageLoader = chapterImageLoader,
            navController = navController,
            snackbarHostState = snackbarHostState,
            profileState = profileState,
        )
        StatusBarProtection()
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun MangaNavHost(
    coverImageLoader: ImageLoader,
    chapterImageLoader: ImageLoader,
    navController: NavHostController = rememberNavController(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    profileState: ProfileState = ProfileState.Unauthenticated,
) {
    val animationSpec = MaterialTheme.motionScheme.slowEffectsSpec<Float>()
    val offsetAnimationSpec = MaterialTheme.motionScheme.slowSpatialSpec<IntOffset>()
    val derivedProfileState = rememberUpdatedState(profileState)
    NavHost(
        navController = navController,
        startDestination = MangaRoutes.Overview,
        enterTransition = {
            slideInHorizontally(offsetAnimationSpec) { it } + fadeIn(animationSpec)
        },
        exitTransition = {
            slideOutHorizontally(offsetAnimationSpec) { -it / 2 }
        },
        popEnterTransition = {
            slideInHorizontally(offsetAnimationSpec) { -it / 2 }
        },
        popExitTransition = {
            slideOutHorizontally(offsetAnimationSpec) { it } + fadeOut(animationSpec)
        },
    ) {
        composable<MangaRoutes.Overview> {
            MangaOverview(
                profileState = profileState,
                showSnackbar = { snackbarHostState.showSnackbar(it) },
                imageLoader = coverImageLoader,
                navigateToProfile = {
                    when (derivedProfileState.value) {
                        is ProfileState.Unauthenticated -> navController.navigate(MangaRoutes.Login)
                        is ProfileState.Authenticated -> navController.navigate(MangaRoutes.Profile)
                    }
                },
                navigateToManga = { mangaId ->
                    navController.navigate(MangaRoutes.Chapters(mangaId))
                },
            )
        }

        composable<MangaRoutes.Profile> {
            ProfileOverview(
                navigateBack = dropUnlessStarted {
                    navController.popBackStack()
                },
                navigateToLogin = dropUnlessStarted {
                    navController.navigate(MangaRoutes.Login)
                },
            )
        }
        composable<MangaRoutes.Login> {
            LoginScreen(
                navigateBack = dropUnlessStarted {
                    when (derivedProfileState.value) {
                        is ProfileState.Unauthenticated -> navController.popBackStack(
                            MangaRoutes.Overview,
                            inclusive = false,
                        )

                        is ProfileState.Authenticated -> {
                            if (!navController.popBackStack(MangaRoutes.Profile, inclusive = false)) {
                                navController.navigate(MangaRoutes.Profile) {
                                    popUpTo(MangaRoutes.Overview)
                                }
                            }
                        }
                    }
                },
                navigateToRegistration = dropUnlessStarted {
                    navController.navigate(MangaRoutes.Registration)
                },
            )
        }
        composable<MangaRoutes.Registration> {
            RegistrationScreen(
                navigateBack = dropUnlessStarted {
                    when (derivedProfileState.value) {
                        is ProfileState.Unauthenticated -> navController.popBackStack()

                        is ProfileState.Authenticated -> {
                            if (!navController.popBackStack(MangaRoutes.Profile, inclusive = false)) {
                                navController.navigate(MangaRoutes.Profile) {
                                    popUpTo(MangaRoutes.Overview)
                                }
                            }
                        }
                    }
                },
            )
        }

        composable<MangaRoutes.Chapters> { backStackEntry ->
            ChapterOverview(
                viewModel = hiltViewModel<ChapterViewModel>(),
                showSnackbar = { snackbarHostState.showSnackbar(it) },
                onBackClick = dropUnlessStarted { navController.popBackStack() },
                navigateToChapter = { chapterId ->
                    val mangaId = backStackEntry.toRoute<MangaRoutes.Chapters>().mangaId
                    navController.navigate(MangaRoutes.Reader(mangaId, chapterId))
                },
            )
        }
        composable<MangaRoutes.Reader> { backStackEntry ->
            ReadChapterScreen(
                imageLoader = chapterImageLoader,
                snackbarHostState = snackbarHostState,
                onBackClick = dropUnlessStarted {
                    navController.popBackStack<MangaRoutes.Chapters>(inclusive = false)
                },
                toChapterClicked = { chapterId ->
                    val mangaId = backStackEntry.toRoute<MangaRoutes.Reader>().mangaId
                    navController.navigate(MangaRoutes.Reader(mangaId, chapterId))
                },
            )
        }
    }
}

@Composable
private fun StatusBarProtection(
    color: Color = MaterialTheme.colorScheme.background,
    heightProvider: () -> Float = calculateGradientHeight(),
) {
    Canvas(Modifier.fillMaxSize()) {
        val calculatedHeight = heightProvider()
        val gradient = Brush.verticalGradient(
            colors = listOf(
                color.copy(alpha = 1f),
                color.copy(alpha = .8f),
                Color.Transparent,
            ),
            startY = 0f,
            endY = calculatedHeight,
        )
        drawRect(
            brush = gradient,
            size = Size(size.width, calculatedHeight),
        )
    }
}

@Composable
fun calculateGradientHeight(): () -> Float {
    val statusBars = WindowInsets.statusBars
    val density = LocalDensity.current
    return { statusBars.getTop(density).times(1.2f) }
}
