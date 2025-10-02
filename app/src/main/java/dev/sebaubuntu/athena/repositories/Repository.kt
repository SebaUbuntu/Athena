/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.repositories

import kotlinx.coroutines.CoroutineScope

abstract class Repository(
    protected val coroutineScope: CoroutineScope,
)
