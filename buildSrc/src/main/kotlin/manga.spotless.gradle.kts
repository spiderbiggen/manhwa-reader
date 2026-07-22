import com.diffplug.gradle.spotless.SpotlessExtension

plugins {
    id("com.diffplug.spotless")
}

private val ktlintVersion = "1.8.0"

val spotlessConfig: SpotlessExtension.() -> Unit = {
    // optional: limit format enforcement to just the files changed by this feature branch
    ratchetFrom("origin/master")

    format("misc") {
        // define the files to apply `misc` to
        target(
            project.fileTree(".") {
                include(
                    "**/.gitattributes",
                    "**/.gitignore",
                    "**/.editorconfig",
                    "**/*.properties",
                    "**/*.md",
                )
                exclude("**/build", "**/node_modules", ".git", ".gradle", ".idea")
                // Skip child projects; they format their own files.
                project.subprojects.forEach {
                    exclude(it.projectDir.toRelativeString(project.projectDir))
                }
            }

        )

        // define the steps to apply to those files
        trimTrailingWhitespace()
        leadingTabsToSpaces(4)
        endWithNewline()
    }
    kotlin {
        target(project.fileTree("src") { include("**/*.kt") })
        // by default the target is every '.kt' and '.kts` file in the java sourcesets
        ktlint(ktlintVersion)
        // has its own section below
        // licenseHeader("/* (C)\$YEAR */") // or licenseHeaderFile
    }
    kotlinGradle {
        target("*.gradle.kts")
        ktlint(ktlintVersion)
    }
}

if (project == rootProject) {
    spotlessPredeclare(spotlessConfig)
}

spotless(spotlessConfig)
