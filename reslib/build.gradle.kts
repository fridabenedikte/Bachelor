plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.emojigame.reslib"
    compileSdk = 35

    defaultConfig {
        minSdk = 30
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}
// Apperantly a better/more reliable version but I can't seem to get it working, don't want to spend
// too long as the version underneath is already working
tasks.register("copyAarToRoot") {
    dependsOn("assembleRelease")

    doLast {
        val outputDir = file("$rootDir/")
        val aarFile = layout.buildDirectory.file("outputs/aar/reslib-release.aar").get().asFile
        if (aarFile.exists()) {
            copy {
                from(aarFile)
                into(outputDir)
            }
            println("AAR copied to: $outputDir")
        } else {
            println("AAR file not found! Check if build succeeded!")
        }
    }
}


/*tasks.withType<AbstractArchiveTask>().matching { it.name == "bundleReleaseAar" }.configureEach {
    destinationDirectory.set(file("$rootDir/"))
}*/

dependencies {
    // No dependency needed for a resource-only library
}
