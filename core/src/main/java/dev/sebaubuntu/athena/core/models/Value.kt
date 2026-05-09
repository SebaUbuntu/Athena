/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.core.models

import android.content.Context
import androidx.annotation.StringRes
import dev.sebaubuntu.athena.core.R
import dev.sebaubuntu.athena.core.ext.hasFlag
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
    class Boolean(value: kotlin.Boolean) : Value<kotlin.Boolean>(value) {
        override fun getDisplayValue(context: Context) = context.getString(
            when (value) {
                true -> R.string.yes
                false -> R.string.no
            }
        )

        override fun toJsonElement() = JsonPrimitive(value)
    }

    class Bitmask(
        value: Int,
        @Transient private val valueToStringResId: Map<Int, Int>,
    ) : Value<Int>(value) {
        override fun getDisplayValue(context: Context): kotlin.String {
            val activeFlags = valueToStringResId.filterKeys { flag ->
                value.hasFlag(flag)
            }.values.map { context.getString(it) }

            return when (activeFlags.isNotEmpty()) {
                true -> activeFlags.joinToString()
                false -> context.getString(R.string.list_no_elements)
            }
        }

        override fun toJsonElement() = JsonPrimitive(value)
    }

    class Bytes(
        value: Long,
    ) : Value<Long>(value) {
        override fun getDisplayValue(context: Context) = "${
            BytesUtils.toHumanReadableSIPrefixes(value)
        } (${
            BytesUtils.toHumanReadableBinaryPrefixes(value)
        })"

        override fun toJsonElement() = JsonPrimitive(value)
    }

    class DateValue(value: Date) : Value<Date>(value) {
        override fun getDisplayValue(context: Context): kotlin.String = dateFormatter.format(value)

        override fun toJsonElement() = JsonPrimitive(value.time)

        companion object {
            private val dateFormatter = SimpleDateFormat("yyyy/MM/dd hh:mm:ss", Locale.US)
        }
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

    class Number<T : kotlin.Number>(
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

    class NumberArray<T : kotlin.Number>(
        value: Array<T>,
        @Transient private val valueToStringResId: Map<T, Int>? = null,
    ) : ArrayValue<T>(value, valueToStringResId, R.string.unknown_value_number) {
        override fun elementToJsonElement(element: T) = JsonPrimitive(element)
    }

    class String(
        value: kotlin.String,
        private val localizedString: LocalizedString? = null,
    ) : Value<kotlin.String>(value) {
        override fun getDisplayValue(
            context: Context,
        ) = localizedString?.getString(context) ?: value

        override fun toJsonElement() = JsonPrimitive(value)
    }

    class StringArray(value: Array<kotlin.String>) : ArrayValue<kotlin.String>(value) {
        override fun elementToJsonElement(element: kotlin.String) = JsonPrimitive(element)
    }

    class UByte(
        value: kotlin.UByte,
        @Transient private val valueToStringResId: Map<kotlin.UByte, Int>? = null,
    ) : UNumber<kotlin.UByte>(value, valueToStringResId) {
        override fun toJsonElement() = JsonPrimitive(value)
    }

    class UInt(
        value: kotlin.UInt,
        @Transient private val valueToStringResId: Map<kotlin.UInt, Int>? = null,
    ) : UNumber<kotlin.UInt>(value, valueToStringResId) {
        override fun toJsonElement() = JsonPrimitive(value)
    }

    class ULong(
        value: kotlin.ULong,
        @Transient private val valueToStringResId: Map<kotlin.ULong, Int>? = null,
    ) : UNumber<kotlin.ULong>(value, valueToStringResId) {
        override fun toJsonElement() = JsonPrimitive(value)
    }

    class UShort(
        value: kotlin.UShort,
        @Transient private val valueToStringResId: Map<kotlin.UShort, Int>? = null,
    ) : UNumber<kotlin.UShort>(value, valueToStringResId) {
        override fun toJsonElement() = JsonPrimitive(value)
    }

    abstract class UNumber<T>(
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

    abstract fun getDisplayValue(context: Context): kotlin.String

    abstract fun toJsonElement(): JsonElement

    override fun equals(other: Any?) = this::class.safeCast(other)?.let {
        this.value == it.value
    } ?: false

    override fun hashCode() = value.hashCode()

    override fun toString() = value.toString()

    companion object {
        operator fun invoke(
            value: Int,
            valueToStringResId: Map<Int, Int>,
        ) = Bitmask(value, valueToStringResId)

        operator fun invoke(value: kotlin.Boolean) = Boolean(value)

        operator fun invoke(value: Date) = DateValue(value)

        inline operator fun <reified T : Enum<T>> invoke(
            value: T,
            valueToStringResId: Map<T, Int>? = null,
        ) = EnumValue(value, valueToStringResId)

        operator fun <T : kotlin.Number> invoke(
            value: T,
            valueToStringResId: Map<T, Int>? = null,
        ) = Number(value, valueToStringResId)

        operator fun <T : kotlin.Number> invoke(
            value: Array<T>,
            valueToStringResId: Map<T, Int>? = null,
        ) = NumberArray(value, valueToStringResId)

        operator fun invoke(
            value: kotlin.String,
            localizedString: LocalizedString? = null,
        ) = String(value, localizedString)

        operator fun invoke(
            value: kotlin.String,
            @StringRes stringResId: Int,
        ) = String(value, LocalizedString(stringResId))

        operator fun invoke(
            value: kotlin.String,
            @StringRes stringResId: Int,
            vararg formatArgs: Any?,
        ) = String(value, LocalizedString(stringResId, *formatArgs))

        operator fun invoke(
            localizedString: LocalizedString,
        ) = String("", localizedString)

        operator fun invoke(value: Array<kotlin.String>) = StringArray(value)

        operator fun invoke(
            value: kotlin.UByte,
            valueToStringResId: Map<kotlin.UByte, Int>? = null,
        ) = UByte(value, valueToStringResId)

        operator fun invoke(
            value: kotlin.UInt,
            valueToStringResId: Map<kotlin.UInt, Int>? = null,
        ) = UInt(value, valueToStringResId)

        operator fun invoke(
            value: kotlin.ULong,
            valueToStringResId: Map<kotlin.ULong, Int>? = null,
        ) = ULong(value, valueToStringResId)

        operator fun invoke(
            value: kotlin.UShort,
            valueToStringResId: Map<kotlin.UShort, Int>? = null,
        ) = UShort(value, valueToStringResId)
    }
}
