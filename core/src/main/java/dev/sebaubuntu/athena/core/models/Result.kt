/*
 * SPDX-FileCopyrightText: The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.core.models

/**
 * Result status. This is very similar to Arrow's `Either<A, B>`
 */
sealed interface Result<T, E> {
    /**
     * The result is ready.
     *
     * @param data The obtained data
     */
    class Success<T, E>(val data: T) : Result<T, E>

    /**
     * The request failed.
     *
     * @param error The error
     * @param throwable An optional [Throwable] object
     */
    class Error<T, E>(val error: E, val throwable: Throwable? = null) : Result<T, E>

    companion object {
        /**
         * Get the data if the result is [Success], null otherwise.
         */
        fun <T, E> Result<T, E>.getOrNull() = when (this) {
            is Success -> data
            is Error -> null
        }

        /**
         * Map the successful result to another [Result] object.
         * On [Error], the original [Result] is returned.
         */
        inline fun <T, E, R> Result<T, E>.flatMap(
            mapping: (T) -> Result<R, E>
        ): Result<R, E> = when (this) {
            is Success -> mapping(data)
            is Error -> Error(error, throwable)
        }

        /**
         * Map the successful result to another type.
         * On [Error], the original [Result] is returned.
         */
        inline fun <T, E, R> Result<T, E>.map(
            mapping: (T) -> R
        ): Result<R, E> = flatMap { Success(mapping(it)) }
    }
}
