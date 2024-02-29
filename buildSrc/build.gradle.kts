plugins {
    `kotlin-dsl`
}

dependencies {
    // Cannot use version catalogs
    implementation(libs.spotless.plugin.gradle)
}
