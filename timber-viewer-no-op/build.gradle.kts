plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("maven-publish")
}

val versionNumber = "0.1.1"

android {
    namespace = "com.github.devapro.logcat.timber"
    compileSdk = 34
    version = versionNumber

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation(libs.timber)
}

publishing {
    publications {
        register<MavenPublication>("release") {
            groupId = "com.github.devapro"
            artifactId = "timber-viewer-no-op"
            version = versionNumber

            afterEvaluate {
                from(components["release"])
            }
        }
    }
}