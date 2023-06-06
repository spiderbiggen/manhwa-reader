plugins {
    kotlin("kapt")
    id("java-library")
    alias(libs.plugins.org.jetbrains.kotlin.jvm)
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation(libs.coroutines.core)

    implementation(libs.dagger)
    kapt(libs.dagger.compiler)
    implementation(libs.kotlinx.datetime)
}

kapt {
    correctErrorTypes = true
}