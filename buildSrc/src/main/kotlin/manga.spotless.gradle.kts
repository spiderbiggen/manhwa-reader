import com.diffplug.gradle.spotless.SpotlessExtensionPredeclare

plugins {
    id("com.diffplug.spotless")
}

private val ktlintVersion = "1.8.0"

if (project == rootProject) {
    spotless { predeclareDeps() }
    configure<SpotlessExtensionPredeclare> {
        kotlin { ktlint(ktlintVersion) }
        kotlinGradle { ktlint(ktlintVersion) }
    }
} else {
    spotless {
        // optional: limit format enforcement to just the files changed by this feature branch
        // ratchetFrom("origin/master")

        format("misc") {
            // define the files to apply `misc` to
            target(
                ".gitattributes",
                "**/.gitignore",
                "**/.editorconfig",
                "**/*.properties",
                "**/*.md",
            )

            // define the steps to apply to those files
            trimTrailingWhitespace()
            leadingTabsToSpaces(4)
            endWithNewline()
        }
        kotlin {
            target("**/*.kt")
            targetExclude("**/build/**/*.kt")
            // by default the target is every '.kt' and '.kts` file in the java sourcesets
            ktlint(ktlintVersion)
            // has its own section below
            // licenseHeader("/* (C)\$YEAR */") // or licenseHeaderFile
        }
        kotlinGradle {
            target("**/*.gradle.kts")
            ktlint(ktlintVersion)
        }
    }
}
