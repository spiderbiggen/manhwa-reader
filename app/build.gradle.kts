plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.daggerHilt)
    alias(libs.plugins.google.ksp)
    id("manga.spotless-conventions")
}

hilt {
    enableAggregatingTask = true
}

android {
    namespace = "com.spiderbiggen.manhwa"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.spiderbiggen.manhwa"
        minSdk = 26
        targetSdk = 34
        versionCode = 5
        versionName = "1.1.2"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        debug {
            isDebuggable = true
            isMinifyEnabled = false
            isShrinkResources = false
            applicationIdSuffix = ".debug"
            versionNameSuffix = ".debug"
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
        create("staging") {
            initWith(getByName("release"))
            signingConfig = signingConfigs.getByName("debug")
            matchingFallbacks += listOf("release", "debug")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin {
        jvmToolchain(17)
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

val stagingImplementation by configurations
dependencies {
    implementation(project(":data"))
    implementation(project(":domain"))
    implementation(project(":presentation"))

    implementation(libs.androidX.core.ktx)

    // Hilt
    ksp(libs.androidX.hilt.compiler)

    // Dagger
    implementation(libs.google.dagger.hiltAndroid)
    ksp(libs.google.dagger.hiltAndroidCompiler)

    implementation(libs.androidX.lifecycle.viewmodel.compose)

    implementation(platform(libs.androidX.compose.bom))
    implementation(libs.androidX.compose.activity)
    implementation(libs.androidX.compose.ui)
    implementation(libs.androidX.compose.uiGraphics)
    implementation(libs.androidX.compose.uiToolingPreview)
    implementation(libs.androidX.compose.material3)
    debugImplementation(libs.androidX.compose.uiTooling)
    stagingImplementation(libs.androidX.compose.uiTooling)
    debugImplementation(libs.androidX.compose.uiTestManifest)

    implementation(platform(libs.coil.bom))
    implementation(libs.coil)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidX.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(platform(libs.androidX.compose.bom))
    androidTestImplementation(libs.androidX.compose.uiTestJunit4)
}
