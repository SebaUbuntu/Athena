/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.sections

import dev.sebaubuntu.athena.R
import dev.sebaubuntu.athena.models.data.Section

object MediaSection : Section(
    "media",
    R.string.section_media_name,
    R.string.section_media_description,
    R.drawable.ic_video_settings,
    navigationActionId = R.id.action_mainFragment_to_mediaFragment,
)
