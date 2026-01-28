import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlinX.compose)
    alias(libs.plugins.google.ksp)
    alias(libs.plugins.google.gms.google.services)
    alias(libs.plugins.google.crashlytics)
    id("manga.spotless")
}

android {
    namespace = "com.spiderbiggen.manga"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.spiderbiggen.manga"
        minSdk = 26
        targetSdk = 36
        versionCode = 70
        versionName = "1.24.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            val properties = Properties().apply {
                try {
                    load(rootProject.rootDir.resolve("local.properties").reader())
                } catch (e: Exception) {
                    System.err.println("Failed to load local.properties: ${e.message}")
                }
            }
            properties.getProperty("signing.keystore")?.let {
                storeFile = rootProject.rootDir.resolve(it)
                storePassword = properties.getProperty("signing.password")
                keyAlias = properties.getProperty("signing.alias")
                keyPassword = properties.getProperty("signing.password")
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
        debug {
            isDebuggable = true
            isMinifyEnabled = false
            isShrinkResources = false
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
        }
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

kotlin {
    jvmToolchain(17)

    compilerOptions {
        freeCompilerArgs.add("-Xreturn-value-checker=full")
    }
}

dependencies {
    implementation(project(":data"))
    implementation(project(":domain"))
    implementation(project(":presentation"))

    implementation(libs.androidX.core.ktx)
    implementation(libs.androidX.core.splashscreen)
    implementation(libs.android.material)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)

    // Koin
    implementation(platform(libs.koin.bom))
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)
    implementation(libs.koin.navigation3)

    // Viewmodel
    implementation(libs.androidX.lifecycle.viewmodel.compose)

    // Compose
    implementation(platform(libs.androidX.compose.bom))
    implementation(libs.androidX.compose.activity)
    implementation(libs.androidX.compose.ui)
    implementation(libs.androidX.compose.uiGraphics)
    implementation(libs.androidX.compose.uiToolingPreview)
    implementation(libs.androidX.compose.material3)
    implementation(libs.androidX.compose.uiTooling)
    implementation(libs.androidX.compose.uiTestManifest)

    // Coil
    implementation(platform(libs.coil.bom))
    implementation(libs.coil)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidX.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(platform(libs.androidX.compose.bom))
    androidTestImplementation(libs.androidX.compose.uiTestJunit4)
}
