/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "dev.sebaubuntu.athena"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "dev.sebaubuntu.athena"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 15
        versionName = "2.0.1"
    }

    buildTypes {
        release {
            // Enables code shrinking, obfuscation, and optimization.
            isMinifyEnabled = true

            // Enables resource shrinking.
            isShrinkResources = true

            // Includes the default ProGuard rules files.
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

        debug {
            // Append .dev to package name so we won't conflict with AOSP build.
            applicationIdSuffix = ".dev"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlin {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(project(":core"))
    implementation(project(":module-audio"))
    implementation(project(":module-biometrics"))
    implementation(project(":module-bluetooth"))
    implementation(project(":module-build"))
    implementation(project(":module-camera"))
    implementation(project(":module-cpu"))
    implementation(project(":module-device"))
    implementation(project(":module-display"))
    implementation(project(":module-drm"))
    implementation(project(":module-gnss"))
    implementation(project(":module-gpu"))
    implementation(project(":module-health"))
    implementation(project(":module-input"))
    implementation(project(":module-media"))
    implementation(project(":module-nfc"))
    implementation(project(":module-ril"))
    implementation(project(":module-security"))
    implementation(project(":module-sensors"))
    implementation(project(":module-services"))
    implementation(project(":module-storage"))
    implementation(project(":module-systemproperties"))
    implementation(project(":module-thermal"))
    implementation(project(":module-treble"))
    implementation(project(":module-user"))
    //implementation(project(":module-uwb"))
    implementation(project(":module-wifi"))

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    //implementation(libs.androidx.compose.material3.adaptive.navigation3)
    debugImplementation(libs.androidx.compose.ui.tooling)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.datastore)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.material)
}
