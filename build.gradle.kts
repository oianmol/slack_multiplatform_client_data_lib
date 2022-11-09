import io.github.timortel.kotlin_multiplatform_grpc_plugin.GrpcMultiplatformExtension.OutputTarget


plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization") version "1.7.20"
    id("com.android.library")
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
                implementation("androidx.security:security-crypto-ktx:1.1.0-alpha03")
                api(project(":slack_generate_protos"))
                implementation("io.github.timortel:grpc-multiplatform-lib-android:0.2.2")
                implementation("io.ktor:ktor-client-android:$ktor_version")
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
    //Specify the folders where your proto files are located, you can list multiple.
    protoSourceFolders.set(listOf(projectDir.parentFile.resolve("slack_protos/src/main/proto")))
}

dependencies {
    commonMainApi("io.github.timortel:grpc-multiplatform-lib:0.2.2")
    commonMainApi(project(Lib.Project.CAPILLARY_KMP))
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