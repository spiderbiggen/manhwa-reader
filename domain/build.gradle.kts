plugins {
    kotlin("kapt")
    id("java-library")
    alias(libs.plugins.org.jetbrains.kotlin.jvm)
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

kotlin {
    jvmToolchain(11)
}

dependencies {
    implementation(libs.coroutines.core)

    implementation(libs.dagger)
    kapt(libs.dagger.compiler)
}

kapt {
    correctErrorTypes = true
}