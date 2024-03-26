/*
 * SPDX-FileCopyrightText: 2023-2024 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.sections

import dev.sebaubuntu.athena.R

object SensorsSection : Section() {
    override val title = R.string.section_sensors_name
    override val description = R.string.section_sensors_description
    override val icon = R.drawable.ic_sensors

    override val navigationActionId = R.id.action_mainFragment_to_sensorsFragment
}
