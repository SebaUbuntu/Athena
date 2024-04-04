/*
 * SPDX-FileCopyrightText: 2023-2024 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.sections

import dev.sebaubuntu.athena.R
import dev.sebaubuntu.athena.models.data.Section

object SensorsSection : Section(
    "sensors",
    R.string.section_sensors_name,
    R.string.section_sensors_description,
    R.drawable.ic_sensors,
    navigationActionId = R.id.action_mainFragment_to_sensorsFragment,
)
