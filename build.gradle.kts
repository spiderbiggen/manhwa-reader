import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.google.ksp) apply false
    alias(libs.plugins.google.gms.google.services) apply false
    alias(libs.plugins.google.crashlytics) apply false
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlinX.compose) apply false
    alias(libs.plugins.kotlinX.serialization) apply false
    alias(libs.plugins.stability.analyzer) apply false
    alias(libs.plugins.detekt) apply false
    alias(libs.plugins.kover)
    alias(libs.plugins.sonarqube)
    id("manga.spotless")
}

buildscript {
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${libs.versions.kotlin}")
        classpath("com.google.devtools.ksp:symbol-processing-gradle-plugin:${libs.versions.ksp}")
    }
}

sonar {
    properties {
        property("sonar.projectKey", "spiderbiggen_manhwa-reader")
        property("sonar.organization", "spiderbiggen")
        property("sonar.coverage.jacoco.xmlReportPaths", "build/reports/coverage/coverage.xml")
    }
    setAndroidVariant("debug")
}

dependencies {
    add("kover", project(":app"))
    add("kover", project(":data"))
    add("kover", project(":domain"))
    add("kover", project(":presentation"))
}

kover {
    reports {
        total {
            filters {
                excludes {
                    classes(
                        "**.BuildConfig",
                        "**.R",
                        "**.R$*",
                        "**.*_Factory",
                        "**.*_MembersInjector",
                    )
                    annotatedBy("androidx.compose.ui.tooling.preview.Preview")
                    annotatedBy("com.spiderbiggen.manga.presentation.coverage.CoverageExcluded")
                }
            }
            xml {
                onCheck = false
                xmlFile = layout.buildDirectory.file("reports/coverage/coverage.xml").get().asFile
            }
            html {
                onCheck = false
                htmlDir = layout.buildDirectory.dir("reports/coverage/html").get().asFile
            }
        }
    }
}

val coverageReportDirectory = layout.buildDirectory.dir("reports/coverage")
val coverageXmlReport = coverageReportDirectory.map { it.file("coverage.xml") }
val coverageHtmlReport = coverageReportDirectory.map { it.dir("html") }

tasks.register<CoverageVerificationTask>("coverage") {
    dependsOn("koverXmlReport", "koverHtmlReport")
    xmlReport.set(coverageXmlReport)
    htmlReport.set(coverageHtmlReport)
}

subprojects {
    pluginManager.withPlugin("org.jetbrains.kotlin.jvm") {
        extensions.configure<KotlinJvmProjectExtension> {
            jvmToolchain(21)
        }
    }
    pluginManager.withPlugin("org.jetbrains.kotlin.android") {
        extensions.configure<KotlinAndroidProjectExtension> {
            jvmToolchain(21)
        }
    }

    sonar {
        setAndroidVariant("debug")
    }
}
