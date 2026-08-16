apply(plugin = "org.jetbrains.kotlinx.kover")

extensions.getByName("kover").withGroovyBuilder {
    "reports" {
        "filters" {
            "excludes" {
                "classes"(
                    "**.BuildConfig",
                    "**.R",
                    "**.R$*",
                    "**.*_Factory",
                    "**.*_MembersInjector",
                )
                "annotatedBy"("androidx.compose.ui.tooling.preview.Preview")
            }
        }
    }
}