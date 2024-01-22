/*
 * SPDX-FileCopyrightText: 2023 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.sections

import dev.sebaubuntu.athena.R

object ThermalSection : Section() {
    override val name = R.string.section_thermal_name
    override val description = R.string.section_thermal_description
    override val icon = R.drawable.ic_thermostat

    override val navigationActionId = R.id.action_mainFragment_to_thermalFragment
}
