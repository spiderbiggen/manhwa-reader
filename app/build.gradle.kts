import java.util.Properties
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlinX.compose)
    alias(libs.plugins.google.daggerHilt)
    alias(libs.plugins.google.ksp)
    alias(libs.plugins.google.gms.google.services)
    alias(libs.plugins.google.crashlytics)
    id("manga.spotless")
}

hilt {
    enableAggregatingTask = true
}

android {
    namespace = "com.spiderbiggen.manga"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.spiderbiggen.manga"
        minSdk = 26
        targetSdk = 36
        versionCode = 57
        versionName = "1.20.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            val properties = Properties().apply {
                load(rootProject.rootDir.resolve("local.properties").reader())
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
        create("staging") {
            initWith(getByName("release"))
            applicationIdSuffix = ".staging"
            versionNameSuffix = "-staging"
            matchingFallbacks += listOf("release", "debug")
        }
        debug {
            isDebuggable = true
            isMinifyEnabled = false
            isShrinkResources = false
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_17
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

    // Dagger
    ksp(libs.google.dagger.hiltAndroidCompiler)
    implementation(libs.google.dagger.hiltAndroid)

    // Hilt
    ksp(libs.androidX.hilt.compiler)

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
