import org.gradle.jvm.toolchain.internal.JavaToolchain
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlinX.serialization)
    alias(libs.plugins.google.ksp)
    id("manga.spotless")
}

android {
    namespace = "com.spiderbiggen.manga.data"
    compileSdk = 36

    defaultConfig {
        minSdk = 26

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }
    buildTypes {
        debug {
            buildConfigField("boolean", "DEBUG", "true")
        }
        release {
            buildConfigField("boolean", "DEBUG", "false")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        buildConfig = true
    }
}

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
    arg("room.incremental", "true")
    arg("room.generateKotlin", "true")
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_17

        freeCompilerArgs.add("-XXLanguage:+PropertyParamAnnotationDefaultTargetMode")
        freeCompilerArgs.add("-Xreturn-value-checker=full")
    }
}

dependencies {
    implementation(project(":domain"))
    implementation(libs.androidX.core.ktx)
    implementation(libs.androidX.appcompat)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)

    // Koin
    implementation(platform(libs.koin.bom))
    implementation(libs.koin.android)

    // Kotlin
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlinX.serialization)
    implementation(libs.kotlinX.datetime)
    implementation(libs.kotlinX.coroutines.android)
    implementation(libs.kotlinX.collections.immutable)

    // Room
    implementation(libs.androidX.room.runtime)
    implementation(libs.androidX.room.ktx)
    ksp(libs.androidX.room.compiler)

    // Datastore
    implementation(libs.androidX.datastore.preferences)

    // Ktor
    implementation(platform(libs.ktor.bom))
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.okhttp)
    implementation(libs.ktor.client.logging)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.client.auth)
    implementation(libs.ktor.client.encoding)

    // Coil
    implementation(platform(libs.coil.bom))
    implementation(libs.coil)

    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.androidX.room.test)
    androidTestImplementation(libs.androidX.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
