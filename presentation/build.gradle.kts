import com.android.build.api.dsl.LibraryExtension

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlinX.compose)
    alias(libs.plugins.kotlinX.serialization)
    alias(libs.plugins.google.ksp)
    id("manga.spotless")
}

configure<LibraryExtension> {
    namespace = "com.spiderbiggen.manga.presentation"
    compileSdk = 36

    defaultConfig {
        minSdk = 26

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
        vectorDrawables {
            useSupportLibrary = true
        }
    }
    buildTypes {
        debug {
            buildConfigField("boolean", "DEBUG", "true")
        }
        release {
            buildConfigField("boolean", "DEBUG", "false")
        }
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

kotlin {
    jvmToolchain(17)
    compilerOptions {
        freeCompilerArgs.add("-Xreturn-value-checker=full")
    }
}

dependencies {
    implementation(project(":domain"))
    // Arrow
    implementation(platform(libs.arrow.bom))
    implementation(libs.arrow.core)
    implementation(libs.arrow.fx.coroutines)

    implementation(libs.androidX.core.ktx)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)

    // Koin
    implementation(platform(libs.koin.bom))
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)
    implementation(libs.koin.navigation3)

    // Kotlin
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlin.reflect)
    implementation(libs.kotlinX.serialization)
    implementation(libs.kotlinX.coroutines.android)
    implementation(libs.kotlinX.datetime)
    implementation(libs.kotlinX.collections.immutable)

    // Lifecycle
    implementation(libs.androidX.lifecycle.runtime.ktx)
    implementation(libs.androidX.lifecycle.runtime.compose)

    // ViewModel
    implementation(libs.androidX.lifecycle.viewmodel.ktx)
    implementation(libs.androidX.lifecycle.viewmodel.compose)
    implementation(libs.androidX.lifecycle.viewmodel.savedstate)

    // Compose
    implementation(platform(libs.androidX.compose.bom))
    implementation(libs.androidX.compose.activity)
    implementation(libs.androidX.compose.ui)
    implementation(libs.androidX.compose.uiGraphics)
    implementation(libs.androidX.compose.material3)
    implementation(libs.androidX.compose.material3.adaptive)
    implementation(libs.androidX.compose.material3.adaptive.layout)
    implementation(libs.androidX.compose.material3.adaptive.navigation3)
    implementation(libs.androidX.compose.animation)
    implementation(libs.androidX.compose.foundation)
    implementation(libs.androidX.compose.uiTooling)
    implementation(libs.androidX.compose.uiTestManifest)
    implementation(libs.androidX.compose.uiToolingPreview)

    // Navigation
    implementation(libs.androidX.navigation3.runtime)
    implementation(libs.androidX.navigation3.ui)
    implementation(libs.androidX.lifecycle.viewmodel.navigation3)

    // Coil
    implementation(platform(libs.coil.bom))
    implementation(libs.coil)
    implementation(libs.coil.compose)
    implementation(libs.coil.network)
    implementation(libs.coil.svg)
    implementation(libs.coil.test)

    // Arrow
    implementation(platform(libs.arrow.bom))
    implementation(libs.arrow.core)
    implementation(libs.arrow.fx.coroutines)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidX.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(platform(libs.androidX.compose.bom))
    androidTestImplementation(libs.androidX.compose.uiTestJunit4)
}
