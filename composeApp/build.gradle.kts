import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

val localProps = Properties().apply {
    val f = rootProject.file("local.properties")
    if (f.exists()) f.inputStream().use { load(it) }
}
val firebaseServiceCreds: String? = localProps.getProperty("firebaseServiceCredentials")
plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)

    id("org.jetbrains.kotlin.plugin.serialization")
    // Add the Google services Gradle plugin
    id("com.google.gms.google-services")
    id("com.google.firebase.appdistribution") version "5.1.1"
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    sourceSets {
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose) // keep Android-only here
            implementation("io.ktor:ktor-client-okhttp:3.0.0")
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)

            implementation("io.ktor:ktor-client-core:3.0.0")
            implementation("io.ktor:ktor-client-content-negotiation:3.0.0")
            implementation("io.ktor:ktor-serialization-kotlinx-json:3.0.0")
            implementation("io.ktor:ktor-client-logging:3.0.0")

            implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
            implementation("dev.gitlive:firebase-common:2.3.0")
            implementation("dev.gitlive:firebase-auth:2.3.0")
            implementation("dev.gitlive:firebase-firestore:2.3.0")
            implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.1")

            // ✅ Coil 3 (multiplatform) + Ktor3 network integration
            implementation("io.coil-kt.coil3:coil-compose:3.3.0")
            implementation("io.coil-kt.coil3:coil-network-ktor3:3.3.0")
        }
        iosMain.dependencies {
            implementation("io.ktor:ktor-client-darwin:3.0.0")
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

android {
    namespace = "org.smartspender.project"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "org.smartspender.project"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            configure<com.google.firebase.appdistribution.gradle.AppDistributionExtension> {
                serviceCredentialsFile = firebaseServiceCreds
                appId = "1:1032360355331:android:2c453e92553665f2839036"
                releaseNotes = "Manual upload from local machine"
                groups = "testers"
            }
        }
        getByName("debug") {
            configure<com.google.firebase.appdistribution.gradle.AppDistributionExtension> {
                serviceCredentialsFile = firebaseServiceCreds
                appId = "1:1032360355331:android:2c453e92553665f2839036"
                releaseNotes = "Debug build"
                groups = "internal"
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    debugImplementation(compose.uiTooling)

    // Firebase BoM (Android)
    implementation(platform("com.google.firebase:firebase-bom:34.3.0"))
    implementation("com.google.firebase:firebase-analytics")
    // Add more Firebase Android deps as needed
}