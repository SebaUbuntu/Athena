/*
 * SPDX-FileCopyrightText: 2024 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.ui.dialogs

import android.content.Context
import android.hardware.Sensor
import android.os.Build
import android.os.Bundle
import com.google.android.material.button.MaterialButton
import dev.sebaubuntu.athena.R
import dev.sebaubuntu.athena.ext.stringRes
import dev.sebaubuntu.athena.ui.views.ListItem
import dev.sebaubuntu.athena.utils.SensorUtils

class SensorInfoAlertDialog(
    context: Context,
    private val sensor: Sensor,
) : CustomAlertDialog(context, R.layout.dialog_sensor_info) {
    private val doneButton by lazy { findViewById<MaterialButton>(R.id.doneButton)!! }
    private val fifoMaxEventCountListItem by lazy { findViewById<ListItem>(R.id.fifoMaxEventCountListItem)!! }
    private val fifoReservedEventCountListItem by lazy { findViewById<ListItem>(R.id.fifoReservedEventCountListItem)!! }
    private val highestDirectReportRateLevelListItem by lazy { findViewById<ListItem>(R.id.highestDirectReportRateLevelListItem)!! }
    private val idListItem by lazy { findViewById<ListItem>(R.id.idListItem)!! }
    private val isAdditionalInfoSupportedListItem by lazy { findViewById<ListItem>(R.id.isAdditionalInfoSupportedListItem)!! }
    private val isDynamicSensorListItem by lazy { findViewById<ListItem>(R.id.isDynamicSensorListItem)!! }
    private val isWakeupSensorListItem by lazy { findViewById<ListItem>(R.id.isWakeupSensorListItem)!! }
    private val maxDelayListItem by lazy { findViewById<ListItem>(R.id.maxDelayListItem)!! }
    private val maximumRangeListItem by lazy { findViewById<ListItem>(R.id.maximumRangeListItem)!! }
    private val minDelayListItem by lazy { findViewById<ListItem>(R.id.minDelayListItem)!! }
    private val nameListItem by lazy { findViewById<ListItem>(R.id.nameListItem)!! }
    private val powerListItem by lazy { findViewById<ListItem>(R.id.powerListItem)!! }
    private val reportingModeListItem by lazy { findViewById<ListItem>(R.id.reportingModeListItem)!! }
    private val resolutionListItem by lazy { findViewById<ListItem>(R.id.resolutionListItem)!! }
    private val stringTypeListItem by lazy { findViewById<ListItem>(R.id.stringTypeListItem)!! }
    private val typeListItem by lazy { findViewById<ListItem>(R.id.typeListItem)!! }
    private val vendorListItem by lazy { findViewById<ListItem>(R.id.vendorListItem)!! }
    private val versionListItem by lazy { findViewById<ListItem>(R.id.versionListItem)!! }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        nameListItem.supportingText = sensor.name

        idListItem.setSupportingTextOrHide(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                sensor.id.takeIf { it > 0 }?.toString()
            } else {
                null
            }
        )

        stringTypeListItem.supportingText = sensor.stringType

        typeListItem.supportingText = "${sensor.type}"

        vendorListItem.supportingText = sensor.vendor

        versionListItem.supportingText = "${sensor.version}"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            isDynamicSensorListItem.setSupportingText(
                sensor.isDynamicSensor.stringRes
            )
        }

        isWakeupSensorListItem.setSupportingText(sensor.isWakeUpSensor.stringRes)

        fifoMaxEventCountListItem.setSupportingTextOrHide(
            sensor.fifoMaxEventCount.takeIf { it > 0 }?.toString()
        )

        fifoReservedEventCountListItem.setSupportingTextOrHide(
            sensor.fifoReservedEventCount.takeIf {
                sensor.fifoMaxEventCount > 0
            }?.toString()
        )

        minDelayListItem.supportingText = "${sensor.minDelay}"

        maxDelayListItem.supportingText = "${sensor.maxDelay}"

        maximumRangeListItem.supportingText = "${sensor.maximumRange}"

        powerListItem.supportingText = "${sensor.power} mA"

        SensorUtils.sensorReportingModeToString[sensor.reportingMode]?.let {
            reportingModeListItem.setSupportingText(it)
        }

        resolutionListItem.supportingText = "${sensor.resolution}"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            isAdditionalInfoSupportedListItem.setSupportingText(
                sensor.isAdditionalInfoSupported.stringRes
            )
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            SensorUtils.sensorDirectReportModeRatesToString[sensor.highestDirectReportRateLevel]?.let {
                highestDirectReportRateLevelListItem.setSupportingText(it)
            }
        }

        doneButton.setOnClickListener {
            dismiss()
        }
    }
}
