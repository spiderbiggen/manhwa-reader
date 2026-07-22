import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.provider.Provider

/** Adds an extra rule set (e.g. compose-rules) to the `detekt` module applied by manga.detekt. */
fun DependencyHandler.detektRuleSet(dependencyNotation: Provider<*>): Dependency? =
    add("detekt", dependencyNotation)
