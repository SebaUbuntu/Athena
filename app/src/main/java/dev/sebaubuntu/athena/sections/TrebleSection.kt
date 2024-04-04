/*
 * SPDX-FileCopyrightText: 2023 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.sections

import dev.sebaubuntu.athena.R
import dev.sebaubuntu.athena.models.data.Section

object TrebleSection : Section(
    "treble",
    R.string.section_treble_name,
    R.string.section_treble_description,
    R.drawable.ic_construction,
    navigationActionId = R.id.action_mainFragment_to_trebleFragment,
)
