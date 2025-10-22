/*
 * SPDX-FileCopyrightText: The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Athena"
include(":app")
include(":core")
include(":module-audio")
include(":module-biometrics")
include(":module-bluetooth")
include(":module-build")
include(":module-camera")
include(":module-cpu")
include(":module-device")
include(":module-display")
include(":module-drm")
include(":module-gnss")
include(":module-gpu")
include(":module-health")
include(":module-input")
include(":module-lights")
include(":module-media")
include(":module-nfc")
include(":module-packages")
include(":module-ril")
include(":module-security")
include(":module-sensors")
include(":module-services")
include(":module-storage")
include(":module-systemproperties")
include(":module-thermal")
include(":module-treble")
include(":module-user")
include(":module-uwb")
include(":module-wifi")
