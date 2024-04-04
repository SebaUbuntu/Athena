/*
 * SPDX-FileCopyrightText: 2023 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.sections

import dev.sebaubuntu.athena.R
import dev.sebaubuntu.athena.models.data.Section

object AudioSection : Section(
    "audio",
    R.string.section_audio_name,
    R.string.section_audio_description,
    R.drawable.ic_audio,
    navigationActionId = R.id.action_mainFragment_to_audioFragment,
)
