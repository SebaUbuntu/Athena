/*
 * SPDX-FileCopyrightText: 2024 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.models.data

import android.content.Context
import androidx.annotation.StringRes
import dev.sebaubuntu.athena.R
import dev.sebaubuntu.athena.ext.stringRes
import dev.sebaubuntu.athena.utils.BytesUtils
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
        @Transient private val valueToStringResId: Map<Int, Int>? = null,
    ) : NumberValue<Int>(int, valueToStringResId, R.string.unknown_value_int)

    data class LongValue(
        private val long: Long,
        @Transient private val valueToStringResId: Map<Long, Int>? = null,
    ) : NumberValue<Long>(long, valueToStringResId, R.string.unknown_value_int)

    class IntArrayValue(
        array: Array<Int>,
        @Transient private val valueToStringResId: Map<Int, Int>? = null,
    ) : ArrayValue<Int>(array, valueToStringResId, R.string.unknown_value_int)

    class FloatArrayValue(array: Array<Float>) : ArrayValue<Float>(array)

    class StringArrayValue(array: Array<String>) : ArrayValue<String>(array)

    data class BytesValue(
        val bytes: Long,
    ) : InformationValue() {
        override fun getValue(context: Context) = bytes.toString()
        override fun getDisplayValue(context: Context) =
            "${
                BytesUtils.toHumanReadableSIPrefixes(bytes)
            } (${
                BytesUtils.toHumanReadableBinaryPrefixes(bytes)
            })"
    }

    class EnumValue<T : Enum<T>>(
        private val enum: T,
        @Transient private val valueToStringResId: Map<T, Int>? = null,
    ) : InformationValue() {
        override fun getValue(context: Context) = enum.name
        override fun getDisplayValue(
            context: Context
        ) = valueToStringResId?.let { valueToStringResId ->
            valueToStringResId[enum]?.let {
                context.getString(it)
            } ?: context.getString(R.string.unknown_value_enum, enum.name, enum.ordinal)
        } ?: getValue(context)
    }

    abstract class NumberValue<T : Number>(
        private val number: T,
        @Transient private val valueToStringResId: Map<T, Int>? = null,
        @Transient @StringRes private val unknownValueStringResId: Int? = null,
    ) : InformationValue() {
        override fun getValue(context: Context) = number.toString()
        override fun getDisplayValue(
            context: Context
        ) = valueToStringResId?.let { valueToStringResId ->
            valueToStringResId[number]?.let {
                context.getString(it)
            } ?: unknownValueStringResId?.let {
                context.getString(it, number)
            }
        } ?: getValue(context)
    }

    abstract class ArrayValue<T>(
        private val array: Array<T>,
        @Transient private val valueToStringResId: Map<T, Int>? = null,
        @Transient @StringRes private val unknownValueStringResId: Int? = null,
    ) : InformationValue() {
        override fun getValue(context: Context) = array.joinToString(", ")
        override fun getDisplayValue(context: Context) = when (array.isEmpty()) {
            true -> context.getString(R.string.list_no_elements)
            false -> valueToStringResId?.let { valueToStringResId ->
                array.joinToString { item ->
                    valueToStringResId[item]?.let {
                        context.getString(it)
                    } ?: unknownValueStringResId?.let {
                        context.getString(it, item)
                    } ?: item.toString()
                }
            } ?: getValue(context)
        }

        override fun equals(other: Any?) = ArrayValue::class.safeCast(other)?.let { o ->
            array.contentEquals(o.array)
        } ?: false

        override fun hashCode() = array.contentHashCode()
    }
}
