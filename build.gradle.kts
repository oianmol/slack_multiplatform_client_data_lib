import io.github.timortel.kotlin_multiplatform_grpc_plugin.GrpcMultiplatformExtension.OutputTarget


plugins {
    kotlin("multiplatform") version "1.7.20"
    kotlin("plugin.serialization") version "1.7.20"
    id("com.android.library")
    id("com.google.protobuf") version "0.8.19"
    id("com.squareup.sqldelight") version "1.5.3"
    id("maven-publish")
    id("io.github.timortel.kotlin-multiplatform-grpc-plugin") version "0.2.2"
}

group = "dev.baseio.slackdatalib"
version = "1.0"



val ktor_version = "2.1.0"

object Jvm {
    val target = JavaVersion.VERSION_1_8
}

object Versions {
    const val koin = "3.1.4"
}


object Deps {

    object Kotlinx {
        const val coroutinesCore = "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4"
        const val datetime = "org.jetbrains.kotlinx:kotlinx-datetime:0.4.0"

        object JVM {
            const val coroutinesSwing = "org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.6.4"
        }

        object IOS {
            const val coroutinesX64 = "org.jetbrains.kotlinx:kotlinx-coroutines-core-iosx64:1.6.4"
            const val coroutinesArm64 = "org.jetbrains.kotlinx:kotlinx-coroutines-core-iosarm64:1.6.4"
        }

        object Android {
            const val coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4"
        }
    }

    object SqlDelight {
        const val androidDriver = "com.squareup.sqldelight:android-driver:1.5.3"
        const val jvmDriver = "com.squareup.sqldelight:sqlite-driver:1.5.3"
        const val nativeDriver = "com.squareup.sqldelight:native-driver:1.5.3"
        const val core = "com.squareup.sqldelight:runtime:1.5.3"
    }


    object Koin {
        const val core = "io.insert-koin:koin-core:${Versions.koin}"
        const val core_jvm = "io.insert-koin:koin-core-jvm:${Versions.koin}"
        const val test = "io.insert-koin:koin-test:${Versions.koin}"
        const val android = "io.insert-koin:koin-android:${Versions.koin}"
    }

    object AndroidX {
        const val lifecycleViewModelKtx = "androidx.lifecycle:lifecycle-viewmodel-ktx:2.5.1"
    }


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
    iosArm64()
    iosSimulatorArm64()
    iosX64()
    
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")
                implementation("com.squareup.sqldelight:runtime:1.5.3")
                implementation(Deps.Koin.core)
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.0")
                implementation(Deps.Kotlinx.datetime)
                implementation(Deps.SqlDelight.core)
                implementation(Deps.Kotlinx.coroutinesCore)
                implementation(Deps.Koin.core)
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
                implementation(Deps.Koin.test)
                implementation(kotlin("test"))
            }
        }
        val androidMain by getting {
            kotlin.srcDirs(
                projectDir.resolve("build/generated/source/kmp-grpc/androidMain/kotlin").canonicalPath,
            )
            dependencies {
                implementation(Deps.Koin.android)
                implementation(Deps.Kotlinx.coroutinesCore)
                implementation(Deps.SqlDelight.androidDriver)
                implementation(Deps.AndroidX.lifecycleViewModelKtx)
                implementation("androidx.security:security-crypto-ktx:1.1.0-alpha03")
                implementation("dev.baseio.slackdatalib:slack-multiplatform-generate-protos:1.0")
                implementation("io.github.timortel:grpc-multiplatform-lib-android:0.2.2")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")
                implementation("io.ktor:ktor-client-android:$ktor_version")
            }
        }
        val iosArm64Main by getting {
            kotlin.srcDirs(
                projectDir.resolve("build/generated/source/kmp-grpc/iosArm64Main/kotlin").canonicalPath,
            )
            dependsOn(sqlDriverNativeMain)
            dependencies {
                implementation("io.ktor:ktor-client-darwin:$ktor_version")
                implementation("com.squareup.sqldelight:native-driver:1.5.3")
            }
        }
        val iosSimulatorArm64Main by getting {
            kotlin.srcDirs(
                projectDir.resolve("build/generated/source/kmp-grpc/iosSimulatorArm64Main/kotlin").canonicalPath,
            )
            dependsOn(sqlDriverNativeMain)

            dependencies {
                implementation("io.ktor:ktor-client-darwin:$ktor_version")
                implementation("com.squareup.sqldelight:native-driver:1.5.3")
            }
        }
        val iosX64Main by getting {
            kotlin.srcDirs(
                projectDir.resolve("build/generated/source/kmp-grpc/iosX64Main/kotlin").canonicalPath,
            )
            dependsOn(sqlDriverNativeMain)

            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-iosx64:1.6.4")
                implementation("io.ktor:ktor-client-darwin:$ktor_version")
                implementation("com.squareup.sqldelight:native-driver:1.5.3")
            }
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
                implementation(Deps.Kotlinx.coroutinesCore)
                implementation(Deps.Kotlinx.JVM.coroutinesSwing)
                implementation(Deps.SqlDelight.jvmDriver)
                implementation("dev.baseio.slackdatalib:slack-multiplatform-generate-protos:1.0")
                implementation("io.github.timortel:grpc-multiplatform-lib-jvm:0.2.2")
                implementation("io.ktor:ktor-client-java:$ktor_version")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.6.4")
                implementation(Deps.Koin.core_jvm)
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
    protoSourceFolders.set(listOf(projectDir.resolve("protos/src/main/proto")))
}

dependencies {
    commonMainApi("io.github.timortel:grpc-multiplatform-lib:0.2.2")
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
        baseline = file("lint-baseline.xml")
    }
    compileSdk = (33)
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