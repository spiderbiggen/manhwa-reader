import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlinX.serialization)
    alias(libs.plugins.google.daggerHilt)
    alias(libs.plugins.google.ksp)
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

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin {
        compilerOptions {
            jvmTarget = JvmTarget.JVM_17
        }
    }
}

hilt {
    enableAggregatingTask = true
}

dependencies {
    implementation(project(":domain"))
    implementation(libs.androidX.core.ktx)
    implementation(libs.androidX.appcompat)

    // Dagger
    implementation(libs.google.dagger.hiltAndroid)
    ksp(libs.google.dagger.hiltAndroidCompiler)

    // Hilt
    implementation(libs.androidX.hilt.common)
    ksp(libs.androidX.hilt.compiler)

    // Kotlin
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlinX.serialization)
    implementation(libs.kotlinX.datetime)
    implementation(libs.kotlinX.coroutines.android)

    // Room
    implementation(libs.androidX.room.runtime)
    implementation(libs.androidX.room.ktx)
    ksp(libs.androidX.room.compiler)
    testImplementation(libs.androidX.room.test)

    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.serialization)
    implementation(platform(libs.okhttp.bom))
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidX.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
