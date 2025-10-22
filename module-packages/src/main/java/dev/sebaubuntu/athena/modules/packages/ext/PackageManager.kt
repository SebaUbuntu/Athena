/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.modules.packages.ext

import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
fun PackageManager.getApplicationInfo(packageName: String, flags: Long) = getApplicationInfo(
    packageName,
    PackageManager.ApplicationInfoFlags.of(flags),
)

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
fun PackageManager.getInstalledApplications(flags: Long) = getInstalledApplications(
    PackageManager.ApplicationInfoFlags.of(flags)
)
