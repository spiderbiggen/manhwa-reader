plugins {
    id("java-library")
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.google.ksp)
    alias(libs.plugins.kotlinX.serialization)
    id("manga.spotless")
    id("com.android.lint")
}

kotlin {
    jvmToolchain(17)
    compilerOptions {
        freeCompilerArgs.add("-Xreturn-value-checker=full")
    }
}

dependencies {
    // Kotlin
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlinX.datetime)
    implementation(libs.kotlinX.coroutines.core)
    implementation(libs.kotlinX.serialization)
    implementation(libs.kotlinX.collections.immutable)

    // Arrow
    implementation(platform(libs.arrow.bom))
    implementation(libs.arrow.core)
    implementation(libs.arrow.fx.coroutines)
}
