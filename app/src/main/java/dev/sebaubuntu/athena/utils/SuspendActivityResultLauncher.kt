/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.utils

import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.core.app.ActivityOptionsCompat
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Suspend-like [ActivityResultLauncher].
 *
 * @see ActivityResultLauncher
 */
class SuspendActivityResultLauncher<I, O, T>(
    activityResultCaller: ActivityResultCaller,
    contract: ActivityResultContract<I, O>,
    private val transform: (O) -> T,
) {
    /**
     * [Mutex] used for synchronizing access to [channel].
     */
    private val mutex = Mutex()

    /**
     * [Channel] used to emit request permissions result. Guarded by [mutex].
     */
    private val channel = Channel<O>(1)

    /**
     * [ActivityResultLauncher] used to request permissions.
     */
    private val activityResultLauncher = activityResultCaller.registerForActivityResult(
        contract = contract,
        callback = channel::trySend,
    )

    /**
     * @see ActivityResultLauncher.launch
     */
    suspend fun launch(input: I, options: ActivityOptionsCompat? = null) = mutex.withLock {
        activityResultLauncher.launch(input, options)
        transform(channel.receive())
    }

    companion object {
        /**
         * Suspend-like [ActivityResultLauncher].
         *
         * @see ActivityResultLauncher
         */
        operator fun <I, O> invoke(
            activityResultCaller: ActivityResultCaller,
            contract: ActivityResultContract<I, O>,
        ) = SuspendActivityResultLauncher(activityResultCaller, contract) { it }
    }
}
