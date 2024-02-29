plugins {
    alias(libs.plugins.com.android.library)
    alias(libs.plugins.org.jetbrains.kotlin.android)
    alias(libs.plugins.com.google.ksp)
    alias(libs.plugins.com.google.hilt.android)
    alias(libs.plugins.org.jetbrains.kotlin.serialization)
    id("manga.spotless-conventions")
}

android {
    namespace = "com.spiderbiggen.manhwa.data"
    compileSdk = 34

    defaultConfig {
        minSdk = 26

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")

        ksp {
            arg("room.schemaLocation", "$projectDir/schemas")
            arg("room.incremental", "true")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin {
        jvmToolchain(17)
    }
}

dependencies {
    implementation(project(":domain"))
    implementation(libs.coroutines.core)
    implementation(libs.core.ktx)
    implementation(libs.hilt)
    ksp(libs.android.hilt.compiler)
    ksp(libs.dagger.hilt.compiler)

    implementation(libs.work)
    implementation(libs.android.hilt.work)
    implementation(libs.android.hilt.common)

    implementation(libs.room)
    implementation(libs.room.ktx)
    ksp(libs.room.processing)
    testImplementation(libs.room.test)

    implementation(libs.appcompat)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation(libs.kotlinx.serialization)
    implementation(libs.kotlinx.datetime)

    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.serialization)
    implementation(platform(libs.okhttp.bom))
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)
}
