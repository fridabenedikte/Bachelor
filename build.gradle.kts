plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.jetbrains.kotlin.jvm) apply false
    alias(libs.plugins.ktlint.plugin) apply true
    alias(libs.plugins.google.services) apply false
}

subprojects {
    if (name != "reslib") {
        apply(plugin = "org.jlleitschuh.gradle.ktlint")

        ktlint {
            android.set(
                project.plugins.hasPlugin("com.android.application") || project.plugins.hasPlugin(
                    "com.android.library"
                )
            )
            outputToConsole.set(true)
            verbose.set(false)
            ignoreFailures.set(false)
            reporters {
                reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.PLAIN)
            }
        }
    }
}

tasks.register("setup") {
    group = "build"
    description = "Builds only the reslib module to generate the AAR."

    dependsOn(":reslib:copyAarToRoot")

    doLast {
        println("✅ reslib built successfully. Now you can run the full build.")
    }
}
