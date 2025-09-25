import org.jreleaser.model.Active

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("maven-publish")
    id("org.jreleaser") version "1.13.1"
    id("signing")
}

val versionNumber = "0.1.2"

android {
    namespace = "com.github.devapro.logcat.timber"
    compileSdk = 34
    version = versionNumber

    defaultConfig {
        aarMetadata {
            minCompileSdk = 34
        }

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
    buildToolsVersion = "34.0.0"
    publishing {
        singleVariant("release") {
            withSourcesJar()
            withJavadocJar()
        }
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.timber)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.recyclerview)
}

publishing {
    repositories {
        maven {
            setUrl(layout.buildDirectory.dir("staging-deploy"))
        }
    }
    publications {
        register<MavenPublication>("release") {
            groupId = "io.github.devapro"
            artifactId = "timber-viewer"
            version = versionNumber

            pom {
                name.set("Timber Log Viewer")
                description.set("Library for viewing Timber logs in a separate window on the device.")
                url.set("https://github.com/devapro/timber-log-viewer")
                issueManagement {
                    url.set("https://github.com/devapro/timber-log-viewer/issues")
                }

                scm {
                    url.set("https://github.com/devapro/timber-log-viewer")
                    connection.set("scm:git://github.com/devapro/timber-log-viewer.git")
                    developerConnection.set("scm:git://github.com/devapro/timber-log-viewer.git")
                }

                licenses {
                    license {
                        name.set("The Apache Software License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                        distribution.set("repo")
                    }
                }

                developers {
                    developer {
                        id.set("devapro")
                        name.set("Arsenii Kharlanov")
                        email.set("arsenyzp@gmail.com")
                        url.set("https://github.com/devapro")
                    }
                }
            }

            afterEvaluate {
                from(components["release"])
            }
        }
    }
}

jreleaser {
    gitRootSearch = true
    release {
        github {
            skipRelease = true
            skipTag = true
        }
    }
    signing {
        active = Active.ALWAYS
        armored = true
        verify = true
    }
    project {
        inceptionYear = "2024"
        author("@devapro")
    }
    deploy {
        maven {
            mavenCentral.create("sonatype") {
                active = Active.ALWAYS
                url = "https://central.sonatype.com/api/v1/publisher"
                stagingRepository(layout.buildDirectory.dir("staging-deploy").get().toString())
                setAuthorization("Basic")
                applyMavenCentralRules = false // Wait for fix: https://github.com/kordamp/pomchecker/issues/21
                sign = true
                checksums = true
                sourceJar = true
                javadocJar = true
                retryDelay = 60
            }
        }
    }
}