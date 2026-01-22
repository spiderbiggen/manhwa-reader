plugins {
    `kotlin-dsl`
}

kotlin {
    jvmToolchain(17)
    compilerOptions {
        freeCompilerArgs.add("-Xreturn-value-checker=full")
    }
}

dependencies {
    implementation(libs.spotless.plugin.gradle)
}
