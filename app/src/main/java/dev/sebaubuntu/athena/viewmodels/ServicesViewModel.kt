/*
 * SPDX-FileCopyrightText: 2023 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import dev.sebaubuntu.athena.utils.SystemProperties

class ServicesViewModel(application: Application) : AndroidViewModel(application) {
    val services
        get() = SystemProperties.props.filterKeys {
            it.startsWith(INIT_SERVICE_PREFIX)
        }.map {
            it.key.removePrefix(INIT_SERVICE_PREFIX) to it.value
        }.toMap()

    companion object {
        private const val INIT_SERVICE_PREFIX = "init.svc."
    }
}
