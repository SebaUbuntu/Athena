/*
 * SPDX-FileCopyrightText: 2023 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.sections

import dev.sebaubuntu.athena.R
import dev.sebaubuntu.athena.models.data.Section

object GpuSection : Section(
    "gpu",
    R.string.section_gpu_name,
    R.string.section_gpu_description,
    R.drawable.ic_display_settings,
    navigationActionId = R.id.action_mainFragment_to_gpuFragment,
)
