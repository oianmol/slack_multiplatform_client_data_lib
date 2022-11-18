import io.github.timortel.kotlin_multiplatform_grpc_plugin.GrpcMultiplatformExtension.OutputTarget


plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization") version "1.7.20"
    id("com.android.library")
    kotlin("native.cocoapods")
    id("com.google.protobuf") version "0.8.19"
    id("com.squareup.sqldelight")
    id("maven-publish")
    id("io.github.timortel.kotlin-multiplatform-grpc-plugin") version "0.2.2"
}

group = "dev.baseio.slackclone"
version = "1.0"



val ktor_version = "2.1.0"

object Jvm {
    val target = JavaVersion.VERSION_1_8
}

repositories {
    mavenCentral()
    mavenLocal()
    google()
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "11"
        }
    }
    android(){
        publishLibraryVariants("release")
    }
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    cocoapods {
        summary = "GRPC Kotlin Multiplatform test library"
        homepage = "https://github.com/TimOrtel/GRPC-Kotlin-Multiplatform"
        ios.deploymentTarget = "14.0"

        pod("gRPC-ProtoRPC", moduleName = "GRPCClient")
        pod("Protobuf")
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")
                implementation("com.squareup.sqldelight:runtime:1.5.3")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.0")
                implementation(Deps.Kotlinx.datetime)
                implementation(Deps.SqlDelight.runtime)
                implementation(Lib.Async.COROUTINES)
                implementation(project(Lib.Project.SLACK_DOMAIN_COMMON))
                implementation(kotlin("stdlib-common"))
                implementation("io.github.timortel:grpc-multiplatform-lib:0.2.2")
            }
            kotlin.srcDirs(
                projectDir.resolve("build/generated/source/kmp-grpc/commonMain/kotlin").canonicalPath,
            )
        }
        val sqlDriverNativeMain by creating {
            dependsOn(commonMain)
            dependencies {
                implementation("com.squareup.sqldelight:native-driver:1.5.3")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val androidMain by getting {
            kotlin.srcDirs(
                projectDir.resolve("build/generated/source/kmp-grpc/jvmMain/kotlin").canonicalPath,
            )
            dependencies {
                implementation(Deps.SqlDelight.androidDriver)
                implementation("androidx.security:security-crypto-ktx:1.1.0-alpha04")
                api(project(":slack_generate_protos"))
                implementation("io.github.timortel:grpc-multiplatform-lib-android:0.2.2")
                implementation("io.ktor:ktor-client-android:$ktor_version")
            }
        }
        val iosArm64Main by getting {
            dependsOn(sqlDriverNativeMain)
            dependencies {
                implementation(Deps.Kotlinx.IOS.coroutinesArm64)
            }
        }
        val iosSimulatorArm64Main by getting {
            dependsOn(sqlDriverNativeMain)
            dependencies {
                implementation(Deps.Kotlinx.IOS.coroutinesiossimulatorarm64)
            }
        }
        val iosX64Main by getting {
            dependsOn(sqlDriverNativeMain)
            dependencies {
                implementation(Deps.Kotlinx.IOS.coroutinesX64)
            }
        }

        val iosMain by creating{
            kotlin.srcDirs(
                projectDir.resolve("build/generated/source/kmp-grpc/iosMain/kotlin").canonicalPath,
            )
            dependencies{
                implementation("com.squareup.sqldelight:native-driver:1.5.3")
                implementation("io.ktor:ktor-client-darwin:$ktor_version")
            }
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
        }


        val androidTest by getting {
            dependencies {
                implementation("junit:junit:4.13.2")
            }
        }
        val jvmMain by getting {
            kotlin.srcDirs(
                projectDir.resolve("build/generated/source/kmp-grpc/jvmMain/kotlin").canonicalPath,
            )
            dependencies {
                implementation(Deps.Kotlinx.JVM.coroutinesSwing)
                implementation(Deps.SqlDelight.jvmDriver)
                api(project(":slack_generate_protos"))
                implementation("io.github.timortel:grpc-multiplatform-lib-jvm:0.2.2")
                implementation("io.ktor:ktor-client-java:$ktor_version")
            }
        }
    }
}

grpcKotlinMultiplatform {
    targetSourcesMap.put(OutputTarget.COMMON, listOf(kotlin.sourceSets.getByName("commonMain")))
    targetSourcesMap.put(
        OutputTarget.JVM,
        listOf(kotlin.sourceSets.getByName("jvmMain"), kotlin.sourceSets.getByName("androidMain"))
    )
    targetSourcesMap.put(
        OutputTarget.IOS,
        listOf(
            kotlin.sourceSets.getByName("iosArm64Main"),
            kotlin.sourceSets.getByName("iosSimulatorArm64Main"),
            kotlin.sourceSets.getByName("iosX64Main")
        )
    )
    //Specify the folders where your proto files are located, you can list multiple.
    protoSourceFolders.set(listOf(projectDir.parentFile.resolve("slack_protos/src/main/proto")))
}

dependencies {
    commonMainApi("io.github.timortel:grpc-multiplatform-lib:0.2.2")
    commonMainApi("dev.baseio.slackcrypto:capillary-kmp:1.0")
}

kotlin {
    targets.withType<org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget> {
        binaries.all {
            // TODO: the current compose binary surprises LLVM, so disable checks for now.
            freeCompilerArgs += "-Xdisable-phases=VerifyBitcode"
        }
    }
}

sqldelight {
    database("SlackDB") {
        packageName = "dev.baseio.database"
        linkSqlite = true
    }
}


android {
    lint{
        this.abortOnError = false
        this.checkReleaseBuilds = false
        //baseline = file("lint-baseline.xml")
    }
    compileSdk = 33
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = (24)
        targetSdk = (33)
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

tasks.replace("podGenIOS", PatchedPodGenTask::class)
