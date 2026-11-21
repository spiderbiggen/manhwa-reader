// Shared Android SDK levels. `targetSdk` stays in the application module because it only
// applies there. The `android` extension is configured through Groovy's dynamic access: AGP is
// not on buildSrc's classpath (it lives in the root project's classloader), and adding it would
// create a second, incompatible copy of its types (see manga.detekt for the same constraint).
val compileSdkMajor = 37
val compileSdkMinor = 2
val minSdkVersion = 26

fun Project.configureAndroid() {
    extensions.getByName("android").withGroovyBuilder {
        "compileSdk" {
            setProperty(
                "version",
                "release"(compileSdkMajor) { setProperty("minorApiLevel", compileSdkMinor) },
            )
        }
        getProperty("defaultConfig").withGroovyBuilder { setProperty("minSdk", minSdkVersion) }
    }
}

pluginManager.withPlugin("com.android.application") { configureAndroid() }

pluginManager.withPlugin("com.android.library") { configureAndroid() }
