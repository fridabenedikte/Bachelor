plugins {
    id("java-library")
    alias(libs.plugins.jetbrains.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
}
java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}
kotlin {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21
    }
}
dependencies {

    implementation(libs.kotlinx.serialization.json)

    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.kotlin.stdlib)
    implementation(kotlin("test"))
    testImplementation(libs.mockk.io)
    testImplementation(libs.bytebuddy.core)
    testImplementation(libs.bytebuddy.agent)
    testImplementation(libs.kotlin.reflect)

    // State management
    implementation(libs.kotlinx.coroutines.core)
}
