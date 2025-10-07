/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.sections

import dev.sebaubuntu.athena.R
import dev.sebaubuntu.athena.models.data.Section

object ServicesSection : Section(
    "services",
    R.string.section_services_name,
    R.string.section_services_description,
    R.drawable.ic_settings_account_box,
    navigationActionId = R.id.action_mainFragment_to_servicesFragment,
)
