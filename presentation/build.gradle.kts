import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlinX.compose)
    alias(libs.plugins.kotlinX.serialization)
    alias(libs.plugins.google.daggerHilt)
    alias(libs.plugins.google.ksp)
    id("manga.spotless")
}

android {
    namespace = "com.spiderbiggen.manga.presentation"
    compileSdk = 35

    defaultConfig {
        minSdk = 26

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin {
        compilerOptions {
            jvmTarget = JvmTarget.JVM_17
        }
    }
    buildFeatures {
        compose = true
    }
}

hilt {
    enableAggregatingTask = true
}

dependencies {
    implementation(project(":domain"))
    implementation(libs.androidX.core.ktx)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)

    // Dagger
    implementation(libs.google.dagger.hiltAndroid)
    ksp(libs.google.dagger.hiltAndroidCompiler)

    // Hilt
    ksp(libs.androidX.hilt.compiler)

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
    implementation(libs.androidX.compose.materialIcons)
    implementation(libs.androidX.compose.uiTooling)
    implementation(libs.androidX.compose.uiTestManifest)
    implementation(libs.androidX.compose.uiToolingPreview)

    // Navigation
    implementation(libs.androidX.hilt.navigationCompose)
    implementation(libs.androidX.navigation.compose)
    implementation(libs.androidX.compose.animation)
    implementation(libs.androidX.compose.foundation)

    // Coil
    implementation(platform(libs.coil.bom))
    implementation(libs.coil)
    implementation(libs.coil.compose)
    implementation(libs.coil.network)
    implementation(libs.coil.test)

    // Material Kolor
    implementation(libs.material.kolor)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidX.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(platform(libs.androidX.compose.bom))
    androidTestImplementation(libs.androidX.compose.uiTestJunit4)
}
