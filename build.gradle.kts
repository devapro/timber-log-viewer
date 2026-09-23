// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        // Required by AGP's SDK package parser when running Gradle on JDK 11+
        classpath("javax.activation:javax.activation-api:1.2.0")
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.jreleaser) apply false
}