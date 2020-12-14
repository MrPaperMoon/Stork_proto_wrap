import com.google.protobuf.gradle.protoc
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `java-library`
    idea
    kotlin("jvm") version "1.4.20"
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

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.slf4j:slf4j-api:1.7.30")
    api("com.google.protobuf:protobuf-java:3.14.0")
    api("com.google.protobuf:protobuf-java-util:3.14.0")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.2")

    implementation(platform("io.ktor:ktor-bom:1.4.3"))
    implementation("io.ktor:ktor-client-core")
    implementation("io.ktor:ktor-client-jackson")
    implementation("io.ktor:ktor-client-okhttp")

    implementation("com.squareup.okhttp3:logging-interceptor:4.2.1")

    implementation("com.tinder.scarlet:scarlet:0.1.10")
    implementation("com.tinder.scarlet:websocket-okhttp:0.1.10")
    implementation("com.tinder.scarlet:message-adapter-protobuf:0.1.10")
    implementation("com.tinder.scarlet:stream-adapter-coroutines:0.1.10")

    testImplementation(platform("org.junit:junit-bom:5.7.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}

// config JVM target to 1.8 for kotlin compilation tasks
tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions.jvmTarget = "1.8"
}

protobuf.protobuf.apply {
    // Configure the protoc executable
    protoc {
        artifact = "com.google.protobuf:protoc:3.14.0"
    }
}

repositories {
    mavenCentral()
}