/*
 * SPDX-FileCopyrightText: 2023 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.sections

import dev.sebaubuntu.athena.R

object AudioSection : Section() {
    override val name = R.string.section_audio_name
    override val description = R.string.section_audio_description
    override val icon = R.drawable.ic_audio

    override val navigationActionId = R.id.action_mainFragment_to_audioFragment
}
