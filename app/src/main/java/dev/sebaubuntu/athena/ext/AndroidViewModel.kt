/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.ext

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel

val AndroidViewModel.applicationContext: Context
    get() = getApplication<Application>().applicationContext
