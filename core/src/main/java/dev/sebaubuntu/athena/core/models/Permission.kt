/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.core.models

/**
 * Android permissions abstraction.
 */
enum class Permission {
    /**
     * Biometrics permissions.
     */
    BIOMETRICS,

    /**
     * Bluetooth permissions.
     */
    BLUETOOTH,

    /**
     * Camera permissions.
     */
    CAMERA,

    /**
     * Internet permissions.
     */
    INTERNET,

    /**
     * Location permissions.
     */
    LOCATION,

    /**
     * NFC permissions.
     */
    NFC,

    /**
     * RIL permissions.
     */
    RIL,

    /**
     * Sensors permissions.
     */
    SENSORS,

    /**
     * UWB permissions.
     */
    UWB,

    /**
     * Wi-Fi permissions.
     */
    WIFI,
}
