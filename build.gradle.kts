import com.google.protobuf.gradle.protoc
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.types.typeUtil.boundClosure

plugins {
    `java-library`
    idea
    kotlin("jvm") version "1.5.10"
    id("com.google.protobuf") version "0.8.13"
}

kotlin {
    sourceSets["main"].apply {
        kotlin.srcDir("build/generated/source/proto/main/kotlin")
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = freeCompilerArgs + "-Xallow-result-return-type"
    }
}

val protobufVersion = "3.12.4"

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))

    implementation("org.slf4j:slf4j-api:1.7.30")
    api("com.google.protobuf:protobuf-java:$protobufVersion")
    api("com.google.protobuf:protobuf-java-util:$protobufVersion")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.2")

    implementation(platform("io.ktor:ktor-bom:1.4.3"))
    implementation("io.ktor:ktor-client-core")
    implementation("io.ktor:ktor-client-jackson")
    implementation("io.ktor:ktor-client-okhttp")

    implementation("com.squareup.okhttp3:logging-interceptor:4.2.1")

    testImplementation(platform("org.junit:junit-bom:5.7.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("io.kotest:kotest-assertions-core:4.6.1")
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

protobuf.protobuf.apply {
    // Configure the protoc executable
    protoc {
        artifact = "com.google.protobuf:protoc:$protobufVersion"
    }
}

repositories {
    mavenCentral()
}