import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)

    id("org.jetbrains.kotlin.plugin.serialization")
    // Firebase for Android
    id("com.google.gms.google-services")
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
        // ========== ANDROID MAIN ==========
        androidMain.dependencies {
            // Jetpack Compose Android-specific
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)

            // ✅ Firebase SDKs for Android (BoM manages versions)
            implementation(project.dependencies.platform("com.google.firebase:firebase-bom:34.3.0"))
            implementation("com.google.firebase:firebase-analytics")
            implementation("com.google.firebase:firebase-auth")
            implementation("com.google.firebase:firebase-firestore")
        }

        // ========== COMMON MAIN ==========
        commonMain.dependencies {
            // Compose Multiplatform shared UI
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
            implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.1")
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
            implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

            // ✅ Multiplatform Firebase APIs (GitLive)
            implementation("dev.gitlive:firebase-common:2.3.0")
            implementation("dev.gitlive:firebase-auth:2.3.0")
            implementation("dev.gitlive:firebase-firestore:2.3.0")
        }

        // ========== COMMON TEST ==========
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

android {
    namespace = "org.smartspender.project"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "org.smartspender.project" // must match Firebase project
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    // Android-only tooling (not in common code)
    debugImplementation(compose.uiTooling)
}