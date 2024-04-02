/*
 * SPDX-FileCopyrightText: 2024 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.models.data

import android.content.Context
import androidx.annotation.StringRes
import dev.sebaubuntu.athena.R
import dev.sebaubuntu.athena.ext.stringRes
import kotlinx.serialization.Transient
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.reflect.safeCast

sealed class InformationValue {
    abstract fun getValue(context: Context): String
    open fun getDisplayValue(context: Context) = getValue(context)

    data class StringValue(
        private val value: String,
    ) : InformationValue() {
        override fun getValue(context: Context) = value
    }

    data class StringResValue(
        @StringRes val resId: Int,
        private val formatArgs: Array<Any>? = null,
        private val value: String? = null,
    ) : InformationValue() {
        override fun getValue(context: Context) = value ?: getDisplayValue(context)
        override fun getDisplayValue(context: Context) = formatArgs?.let {
            context.getString(resId, *it)
        } ?: context.getString(resId)

        override fun equals(other: Any?) = StringResValue::class.safeCast(other)?.let { o ->
            resId == o.resId
                    && formatArgs?.let {
                        o.formatArgs?.let { oFormatArgs ->
                            it.contentEquals(oFormatArgs)
                        } ?: false
                    } ?: (o.formatArgs == null)
                    && value == o.value
        } ?: false

        override fun hashCode(): Int {
            var result = resId
            result = 31 * result + (formatArgs?.contentHashCode() ?: 0)
            result = 31 * result + (value?.hashCode() ?: 0)
            return result
        }
    }

    data class BooleanValue(
        private val boolean: Boolean,
    ) : InformationValue() {
        override fun getValue(context: Context) = boolean.toString()
        override fun getDisplayValue(context: Context) = context.getString(boolean.stringRes)
    }

    data class DateValue(
        private val date: Date,
    ) : InformationValue() {
        override fun getValue(context: Context) = date.time.toString()
        override fun getDisplayValue(context: Context): String = dateFormatter.format(date)

        companion object {
            private val dateFormatter = SimpleDateFormat("yyyy/MM/dd hh:mm:ss", Locale.US)
        }
    }

    data class IntValue(
        private val int: Int,
        @Transient private val stringResIdsMap: Map<Int, Int>? = null,
    ) : InformationValue() {
        override fun getValue(context: Context) = int.toString()
        override fun getDisplayValue(context: Context) = stringResIdsMap?.let {
            context.getString(
                it.getOrElse(int) { R.string.unknown_value }
            )
        } ?: getValue(context)
    }

    data class IntArrayValue(
        private val intArray: Array<Int>,
        @Transient private val stringResIdsMap: Map<Int, Int>? = null,
    ) : InformationValue() {
        override fun getValue(context: Context) = intArray.joinToString(", ")
        override fun getDisplayValue(context: Context) = stringResIdsMap?.let { stringResIdsMap ->
            intArray.joinToString {
                context.getString(
                    stringResIdsMap.getOrElse(it) { R.string.unknown_value },
                    it,
                )
            }
        } ?: getValue(context)

        override fun equals(other: Any?) = IntArrayValue::class.safeCast(other)?.let { o ->
            intArray.contentEquals(o.intArray)
        } ?: false

        override fun hashCode() = intArray.contentHashCode()
    }
}
