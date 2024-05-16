import com.diffplug.gradle.spotless.SpotlessExtensionPredeclare

plugins {
    id("com.diffplug.spotless")
}

if (project == rootProject) {
    spotless { predeclareDeps() }
    configure<SpotlessExtensionPredeclare> {
        kotlin { ktlint("1.2.1") }
        kotlinGradle { ktlint("1.2.1") }
    }
} else {
    spotless {
        // optional: limit format enforcement to just the files changed by this feature branch
        ratchetFrom("master")

        format("misc") {
            // define the files to apply `misc` to
            target(
                ".gitattributes",
                "**/.gitignore",
                "**/.editorconfig",
                "**/*.properties",
                "**/*.md"
            )

            // define the steps to apply to those files
            trimTrailingWhitespace()
            indentWithSpaces(4)
            endWithNewline()
        }
        kotlin {
            target("**/*.kt")
            targetExclude("**/build/**/*.kt")
            // by default the target is every '.kt' and '.kts` file in the java sourcesets
            ktlint("1.2.1")
            // has its own section below
            // licenseHeader("/* (C)\$YEAR */") // or licenseHeaderFile
        }
        kotlinGradle {
            target("**/*.gradle.kts")
            ktlint("1.2.1")
        }
    }
}
