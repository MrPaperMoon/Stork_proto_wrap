import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  id("org.jetbrains.kotlin.jvm")
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

// config JVM target to 1.8 for kotlin compilation tasks
tasks.withType<KotlinCompile>().configureEach {
  kotlinOptions.jvmTarget = "1.8"
}

repositories {
  mavenCentral()
}

dependencies {
  api("com.squareup.wire:wire-runtime:3.7.0")
  api("com.squareup.wire:wire-grpc-client:3.7.0")
  api("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.5.10")
}