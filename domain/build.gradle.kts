import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("java-library")
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.google.ksp)
    id("manga.spotless")
    id("com.android.lint")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_17
    }
}

dependencies {
    // Dagger
    implementation(libs.google.dagger)
    ksp(libs.google.dagger.hiltAndroidCompiler)

    // Kotlin
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlinX.datetime)
    implementation(libs.kotlinX.coroutines.core)
}
