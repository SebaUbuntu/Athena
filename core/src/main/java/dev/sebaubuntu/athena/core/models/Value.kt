/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.core.models

import android.content.Context
import androidx.annotation.StringRes
import dev.sebaubuntu.athena.core.R
import dev.sebaubuntu.athena.core.utils.BytesUtils
import dev.sebaubuntu.athena.core.utils.FrequencyUtils
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.reflect.safeCast

@OptIn(ExperimentalSerializationApi::class)
sealed class Value<T>(val value: T) {
    abstract fun getDisplayValue(context: Context): String

    abstract fun toJsonElement(): JsonElement

    override fun equals(other: Any?) = this::class.safeCast(other)?.let {
        this.value == it.value
    } ?: false

    override fun hashCode() = value.hashCode()

    override fun toString() = value.toString()

    class StringValue(
        value: String,
        private val localizedString: LocalizedString? = null,
    ) : Value<String>(value) {
        override fun getDisplayValue(
            context: Context,
        ) = localizedString?.getString(context) ?: value

        override fun toJsonElement() = JsonPrimitive(value)
    }

    class BooleanValue(value: Boolean) : Value<Boolean>(value) {
        override fun getDisplayValue(context: Context) = context.getString(
            when (value) {
                true -> R.string.yes
                false -> R.string.no
            }
        )

        override fun toJsonElement() = JsonPrimitive(value)
    }

    class DateValue(value: Date) : Value<Date>(value) {
        override fun getDisplayValue(context: Context): String = dateFormatter.format(value)

        override fun toJsonElement() = JsonPrimitive(value.time)

        companion object {
            private val dateFormatter = SimpleDateFormat("yyyy/MM/dd hh:mm:ss", Locale.US)
        }
    }

    class NumberValue<T : Number>(
        value: T,
        @Transient private val valueToStringResId: Map<T, Int>? = null,
    ) : Value<T>(value) {
        override fun getDisplayValue(
            context: Context
        ) = valueToStringResId?.let { valueToStringResId ->
            valueToStringResId[value]?.let {
                context.getString(it)
            } ?: context.getString(R.string.unknown_value_number, value)
        } ?: value.toString()

        override fun toJsonElement() = JsonPrimitive(value)
    }

    class UByteValue(
        value: UByte,
        @Transient private val valueToStringResId: Map<UByte, Int>? = null,
    ) : UNumberValue<UByte>(value, valueToStringResId) {
        override fun toJsonElement() = JsonPrimitive(value)
    }

    class UIntValue(
        value: UInt,
        @Transient private val valueToStringResId: Map<UInt, Int>? = null,
    ) : UNumberValue<UInt>(value, valueToStringResId) {
        override fun toJsonElement() = JsonPrimitive(value)
    }

    class ULongValue(
        value: ULong,
        @Transient private val valueToStringResId: Map<ULong, Int>? = null,
    ) : UNumberValue<ULong>(value, valueToStringResId) {
        override fun toJsonElement() = JsonPrimitive(value)
    }

    class UShortValue(
        value: UShort,
        @Transient private val valueToStringResId: Map<UShort, Int>? = null,
    ) : UNumberValue<UShort>(value, valueToStringResId) {
        override fun toJsonElement() = JsonPrimitive(value)
    }

    class FloatArrayValue(value: Array<Float>) : ArrayValue<Float>(value) {
        override fun elementToJsonElement(element: Float) = JsonPrimitive(element)
    }

    class IntArrayValue(
        value: Array<Int>,
        @Transient private val valueToStringResId: Map<Int, Int>? = null,
    ) : ArrayValue<Int>(value, valueToStringResId, R.string.unknown_value_number) {
        override fun elementToJsonElement(element: Int) = JsonPrimitive(element)
    }

    class StringArrayValue(value: Array<String>) : ArrayValue<String>(value) {
        override fun elementToJsonElement(element: String) = JsonPrimitive(element)
    }

    class BytesValue(
        value: Long,
    ) : Value<Long>(value) {
        override fun getDisplayValue(context: Context) =
            "${
                BytesUtils.toHumanReadableSIPrefixes(value)
            } (${
                BytesUtils.toHumanReadableBinaryPrefixes(value)
            })"

        override fun toJsonElement() = JsonPrimitive(value)
    }

    class EnumValue<T : Enum<T>>(
        value: T,
        private val valueToStringResId: Map<T, Int>? = null,
    ) : Value<T>(value) {
        override fun getDisplayValue(
            context: Context
        ) = valueToStringResId?.let { valueToStringResId ->
            valueToStringResId[value]?.let {
                context.getString(it)
            } ?: context.getString(R.string.unknown_value_enum, value.name, value.ordinal)
        } ?: value.name

        override fun toJsonElement() = JsonPrimitive(value.name)
    }

    class FrequencyValue(
        valueHz: Long,
    ) : Value<Long>(valueHz) {
        override fun getDisplayValue(context: Context) = FrequencyUtils.toHumanReadable(value)

        override fun toJsonElement() = JsonPrimitive(value)
    }

    abstract class UNumberValue<T>(
        value: T,
        @Transient private val valueToStringResId: Map<T, Int>? = null,
    ) : Value<T>(value) {
        override fun getDisplayValue(
            context: Context
        ) = valueToStringResId?.let { valueToStringResId ->
            valueToStringResId[value]?.let {
                context.getString(it)
            } ?: context.getString(R.string.unknown_value_number, value)
        } ?: value.toString()
    }

    abstract class ArrayValue<T : Any>(
        value: Array<T>,
        @Transient private val valueToStringResId: Map<T, Int>? = null,
        @Transient @StringRes private val unknownValueStringResId: Int? = null,
    ) : Value<Array<T>>(value) {
        abstract fun elementToJsonElement(element: T): JsonElement

        override fun getDisplayValue(context: Context) = when (value.isEmpty()) {
            true -> context.getString(R.string.list_no_elements)
            false -> valueToStringResId?.let { valueToStringResId ->
                value.joinToString { item ->
                    valueToStringResId[item]?.let {
                        context.getString(it)
                    } ?: unknownValueStringResId?.let {
                        context.getString(it, item)
                    } ?: item.toString()
                }
            } ?: value.joinToString()
        }

        override fun toJsonElement() = JsonArray(value.map { elementToJsonElement(it) })

        override fun equals(other: Any?) = ArrayValue::class.safeCast(other)?.let {
            value.contentEquals(it.value)
        } ?: false

        override fun hashCode() = value.contentHashCode()
    }

    companion object {
        operator fun invoke(
            value: String,
            localizedString: LocalizedString? = null,
        ) = StringValue(value, localizedString)

        operator fun invoke(
            value: String,
            @StringRes stringResId: Int,
        ) = StringValue(value, LocalizedString(stringResId))

        operator fun invoke(
            value: String,
            @StringRes stringResId: Int,
            vararg formatArgs: Any?,
        ) = StringValue(value, LocalizedString(stringResId, *formatArgs))

        operator fun invoke(
            localizedString: LocalizedString,
        ) = StringValue("", localizedString)

        operator fun invoke(value: Boolean) = BooleanValue(value)

        operator fun invoke(value: Date) = DateValue(value)

        operator fun <T : Number> invoke(
            value: T,
            valueToStringResId: Map<T, Int>? = null,
        ) = NumberValue(value, valueToStringResId)

        operator fun invoke(
            value: UByte,
            valueToStringResId: Map<UByte, Int>? = null,
        ) = UByteValue(value, valueToStringResId)

        operator fun invoke(
            value: UInt,
            valueToStringResId: Map<UInt, Int>? = null,
        ) = UIntValue(value, valueToStringResId)

        operator fun invoke(
            value: ULong,
            valueToStringResId: Map<ULong, Int>? = null,
        ) = ULongValue(value, valueToStringResId)

        operator fun invoke(
            value: UShort,
            valueToStringResId: Map<UShort, Int>? = null,
        ) = UShortValue(value, valueToStringResId)

        operator fun invoke(
            value: Array<Int>,
            valueToStringResId: Map<Int, Int>? = null,
        ) = IntArrayValue(value, valueToStringResId)

        operator fun invoke(value: Array<Float>) = FloatArrayValue(value)

        operator fun invoke(value: Array<String>) = StringArrayValue(value)

        inline operator fun <reified T : Enum<T>> invoke(
            value: T,
            valueToStringResId: Map<T, Int>? = null,
        ) = EnumValue(value, valueToStringResId)
    }
}
