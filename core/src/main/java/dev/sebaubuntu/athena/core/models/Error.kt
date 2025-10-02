/*
 * SPDX-FileCopyrightText: The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.core.models

/**
 * Generic errors definitions for operations result.
 */
enum class Error {
    /**
     * This feature isn't implemented.
     */
    NOT_IMPLEMENTED,

    /**
     * I/O error, can also be network.
     */
    IO,

    /**
     * Authentication error.
     */
    AUTHENTICATION_REQUIRED,

    /**
     * Invalid credentials.
     */
    INVALID_CREDENTIALS,

    /**
     * The item was not found.
     */
    NOT_FOUND,

    /**
     * Value returned on write requests: The value already exists.
     */
    ALREADY_EXISTS,

    /**
     * Response deserialization error.
     */
    DESERIALIZATION,

    /**
     * The request was cancelled.
     */
    CANCELLED,

    /**
     * The server returned an invalid response.
     */
    INVALID_RESPONSE,
}
