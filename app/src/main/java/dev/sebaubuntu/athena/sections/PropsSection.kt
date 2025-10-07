/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.sections

import dev.sebaubuntu.athena.R
import dev.sebaubuntu.athena.models.data.Section

object PropsSection : Section(
    "props",
    R.string.section_props_name,
    R.string.section_props_description,
    R.drawable.ic_build,
    navigationActionId = R.id.action_mainFragment_to_propsFragment,
)
