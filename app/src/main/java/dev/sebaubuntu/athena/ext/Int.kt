/*
 * SPDX-FileCopyrightText: 2023 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.ext

import android.content.res.Resources.getSystem
import kotlin.math.roundToInt

val Int.dp: Int
    get() = (this * getSystem().displayMetrics.density).roundToInt()
