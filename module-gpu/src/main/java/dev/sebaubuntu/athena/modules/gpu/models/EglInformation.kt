/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.modules.gpu.models

data class EglInformation(
    val vendor: String?,
    val version: String?,
    val extensions: List<String>?,
    val clientApi: List<String>?,
) {
    constructor(
        vendor: String?,
        version: String?,
        extensions: String?,
        clientApi: String?,
    ) : this(
        vendor,
        version,
        extensions?.split(" "),
        clientApi?.split(" "),
    )
}
