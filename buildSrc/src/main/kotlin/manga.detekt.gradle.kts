import org.gradle.api.artifacts.VersionCatalogsExtension

// `dev.detekt` is resolved from the version catalog alias applied (unapplied) at the root
// project, so every module that applies it shares one classloader with the Kotlin/Android
// Gradle Plugin - detekt reaches into those classes directly and breaks otherwise. Configured
// through Groovy's dynamic property access so this script never needs `DetektExtension` on its
// own (buildSrc) classpath, which would be a second, incompatible classloader for that type.
apply(plugin = "dev.detekt")

extensions.getByName("detekt").withGroovyBuilder {
    setProperty("buildUponDefaultConfig", true)
    setProperty("parallel", true)
    setProperty("config", files(rootProject.file("config/detekt/detekt.yml")))
    setProperty("source", project.fileTree("src") { include("**/*.kt", "**/*.kts") })
    setProperty("baseline", project.file("detekt-baseline.xml"))
}

// Gradle only adds the detekt CLI to the `detekt` configuration when nothing else has been
// added to it yet. Modules that add rule sets to that same configuration (e.g. compose-rules,
// via `detektRuleSet(...)`) would otherwise end up with an incomplete classpath missing the CLI.
val detektVersion = project.extensions.getByType<VersionCatalogsExtension>()
    .named("libs")
    .findVersion("detekt")
    .get()
    .requiredVersion
dependencies.add("detekt", "dev.detekt:detekt-cli:$detektVersion")
