/*
 * SPDX-FileCopyrightText: 2023 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.sections

import dev.sebaubuntu.athena.R

object PropsSection : Section() {
    override val title = R.string.section_props_name
    override val description = R.string.section_props_description
    override val icon = R.drawable.ic_build

    override val navigationActionId = R.id.action_mainFragment_to_propsFragment
}
