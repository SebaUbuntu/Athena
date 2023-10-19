/*
 * SPDX-FileCopyrightText: 2023 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import dev.sebaubuntu.athena.utils.SystemProperties

class PropsViewModel(application: Application) : AndroidViewModel(application) {
    val props
        get() = SystemProperties.props
}
