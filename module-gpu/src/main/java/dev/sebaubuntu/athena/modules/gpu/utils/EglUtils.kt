/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.modules.gpu.utils

import dev.sebaubuntu.athena.modules.gpu.models.EglInformation

object EglUtils {
    external fun getEglInformation(): EglInformation?
}
