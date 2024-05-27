// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.google.ksp) apply false
    alias(libs.plugins.google.daggerHilt) apply false
    alias(libs.plugins.google.gms.google.services) apply false
    alias(libs.plugins.google.crashlytics) apply false
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlinX.compose) apply false
    alias(libs.plugins.kotlinX.serialization) apply false
    id("manga.spotless")
}
