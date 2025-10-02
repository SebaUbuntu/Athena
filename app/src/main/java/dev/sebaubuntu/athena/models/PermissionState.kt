/*
 * SPDX-FileCopyrightText: The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.models

/**
 * State of a permission.
 */
enum class PermissionState {
    /**
     * The permission is granted.
     */
    GRANTED,

    /**
     * The permission is not granted and the user hasn't been asked for it.
     */
    NOT_GRANTED,

    /**
     * The user explicitly denied the permission.
     */
    DENIED,
}
