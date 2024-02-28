import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.com.google.ksp) apply false
    alias(libs.plugins.com.google.hilt.android) apply false
    alias(libs.plugins.com.android.application) apply false
    alias(libs.plugins.com.android.library) apply false
    alias(libs.plugins.org.jetbrains.kotlin.android) apply false
    alias(libs.plugins.org.jetbrains.kotlin.jvm) apply false
    alias(libs.plugins.org.jetbrains.kotlin.serialization) apply false
    alias(libs.plugins.versions)
    alias(libs.plugins.spotless)
}

fun isNonStable(version: String): Boolean {
    val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { version.uppercase().contains(it) }
    val regex = "^[0-9,.v-]+(-r)?$".toRegex()
    val isStable = stableKeyword || regex.matches(version)
    return isStable.not()
}

// https://github.com/ben-manes/gradle-versions-plugin
tasks.withType<DependencyUpdatesTask> {
    checkConstraints = true
    checkBuildEnvironmentConstraints = true
    rejectVersionIf {
        isNonStable(candidate.version) && !isNonStable(currentVersion)
    }
}

spotless {
    // optional: limit format enforcement to just the files changed by this feature branch
    ratchetFrom("origin/master")

    format("misc") {
        // define the files to apply `misc` to
        target(".gitattributes", ".gitignore")

        // define the steps to apply to those files
        trimTrailingWhitespace()
        indentWithTabs()
        endWithNewline()
    }
    kotlin {
        // by default the target is every '.kt' and '.kts` file in the java sourcesets
        ktlint(libs.versions.ktlint.get())   // has its own section below
//        licenseHeader("/* (C)\$YEAR */") // or licenseHeaderFile
    }
    kotlinGradle {
        target("*.gradle.kts")
        ktlint(libs.versions.ktlint.get())
    }
}
