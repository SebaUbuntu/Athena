/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.ext

import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.contract.ActivityResultContract
import dev.sebaubuntu.athena.utils.SuspendActivityResultLauncher

/**
 * @see ActivityResultCaller.registerForActivityResult
 */
fun <I, O, T> ActivityResultCaller.registerForSuspendActivityResult(
    contract: ActivityResultContract<I, O>,
    transform: (O) -> T,
) = SuspendActivityResultLauncher(this, contract, transform)

/**
 * @see ActivityResultCaller.registerForActivityResult
 */
fun <I, O> ActivityResultCaller.registerForSuspendActivityResult(
    contract: ActivityResultContract<I, O>,
) = SuspendActivityResultLauncher(this, contract)
