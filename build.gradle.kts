import com.google.protobuf.gradle.protoc
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `java-library`
    idea
    kotlin("jvm") version "1.3.72"
    id("com.google.protobuf") version "0.8.13"
}

kotlin {
    sourceSets["main"].apply {
        kotlin.srcDir("build/generated/source/proto/main/java")
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = freeCompilerArgs + "-Xallow-result-return-type"
    }
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.2")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-protobuf:2.9.0")
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
        artifact = "com.google.protobuf:protoc:3.0.0"
    }
}
repositories {
    mavenCentral()
}