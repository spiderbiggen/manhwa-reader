plugins {
    id("com.diffplug.spotless")
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
        ktlint("1.2.0")   // has its own section below
//        licenseHeader("/* (C)\$YEAR */") // or licenseHeaderFile
    }
    kotlinGradle {
        target("*.gradle.kts")
        ktlint("1.2.0")
    }
}
