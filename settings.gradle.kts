pluginManagement {
    repositories {
        google()
        jcenter()
        gradlePluginPortal()
        mavenCentral()
        mavenLocal()
    }
    plugins {
        kotlin("multiplatform") version "1.7.20"
        kotlin("plugin.serialization") version "1.7.20"
        id("com.android.library")
        id("com.google.protobuf") version "0.8.19"
        id("com.squareup.sqldelight") version "1.5.3"
        id("maven-publish")
        id("io.github.timortel.kotlin-multiplatform-grpc-plugin") version "0.2.2"
    }

    resolutionStrategy {
        eachPlugin {
            if (requested.id.namespace == "com.android" || requested.id.name == "kotlin-android-extensions") {
                useModule("com.android.tools.build:gradle:7.2.2")
            }
        }
    }
}


include(":generate-proto")
include(":protos")

rootProject.name = "slack_multiplatform_client_data_lib"

