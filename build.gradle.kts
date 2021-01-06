import org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
import org.gradle.api.tasks.testing.logging.TestLogEvent.*
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.jetbrains.kotlin.jvm") version "1.5.31"
    id("com.github.ben-manes.versions") version "0.39.0"
}

repositories {
    mavenCentral()
}

object Versions {
    const val junit = "5.8.1"
    const val atrium = "0.16.0"
    const val mockk = "1.12.0"
    const val kotlinCoroutines = "1.5.2"
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.kotlinCoroutines}")

    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-junit5"))
    testImplementation("org.junit.jupiter:junit-jupiter:${Versions.junit}")
    testImplementation("ch.tutteli.atrium:atrium-fluent-en_GB:${Versions.atrium}")
    testImplementation("io.mockk:mockk:${Versions.mockk}")
}

val kotlinOptions: KotlinJvmOptions.() -> Unit = {
    jvmTarget = "16"
}
val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions(kotlinOptions)
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions(kotlinOptions)

tasks.test {
    useJUnitPlatform()
    testLogging {
        lifecycle {
            events = setOf(FAILED, PASSED, SKIPPED)
            exceptionFormat = FULL
            showExceptions = true
            showCauses = true
            showStackTraces = true
            showStandardStreams = true
        }
    }
}
