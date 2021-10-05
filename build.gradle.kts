import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `java-library`
    idea
    kotlin("jvm") version "1.5.10"
    id("com.squareup.wire") version "3.7.0"
}

wire {
    kotlin {
        javaInterop = true
    }
}

kotlin {
    sourceSets["main"].apply {
        kotlin.srcDir("build/generated/source/wire")
    }
}

idea.module {
    sourceDirs = sourceDirs + file("src/main/proto")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs = freeCompilerArgs + "-Xallow-result-return-type"
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))

    implementation("org.slf4j:slf4j-api:1.7.30")
    api("com.squareup.wire:wire-runtime:3.7.0")
    api("com.squareup.wire:wire-grpc-client:3.7.0")
    api("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.5.10")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.1")

    implementation(platform("io.ktor:ktor-bom:1.6.4"))
    implementation("io.ktor:ktor-client-core")
    implementation("io.ktor:ktor-client-gson")
    implementation("com.squareup.wire:wire-gson-support:3.7.0")
    implementation("io.ktor:ktor-client-okhttp")

    implementation("com.squareup.okhttp3:logging-interceptor:4.2.1")

    testImplementation(platform("org.junit:junit-bom:5.7.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("io.kotest:kotest-assertions-core:4.6.1")
    testImplementation("org.apache.logging.log4j:log4j-core:2.14.1")
    testImplementation("org.apache.logging.log4j:log4j-slf4j-impl:2.14.1")
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
        exceptionFormat = TestExceptionFormat.FULL
        showExceptions = true
        showCauses = true
        showStackTraces = true

        debug {
            events = setOf(
                TestLogEvent.STARTED,
                TestLogEvent.FAILED,
                TestLogEvent.PASSED,
                TestLogEvent.SKIPPED,
                TestLogEvent.STANDARD_ERROR,
                TestLogEvent.STANDARD_OUT
            )
            exceptionFormat = TestExceptionFormat.FULL
        }
        info.events = debug.events
        info.exceptionFormat = debug.exceptionFormat
    }
}

// config JVM target to 1.8 for kotlin compilation tasks
tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions.jvmTarget = "1.8"
}

repositories {
    mavenCentral()
}