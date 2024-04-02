/*
 * SPDX-FileCopyrightText: 2023 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.sections

import dev.sebaubuntu.athena.R
import dev.sebaubuntu.athena.models.data.Section

object MediaSection : Section() {
    override val title = R.string.section_media_name
    override val description = R.string.section_media_description
    override val icon = R.drawable.ic_video_settings

    override val navigationActionId = R.id.action_mainFragment_to_mediaFragment
}
