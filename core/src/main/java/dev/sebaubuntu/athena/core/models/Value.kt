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
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.builtins.ArraySerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.serializer
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.reflect.KClass
import kotlin.reflect.safeCast

@Serializable(with = Value.Companion.Serializer::class)
sealed class Value<T>(val value: T) {
    abstract val valueSerializer: KSerializer<T>

    abstract fun getDisplayValue(context: Context): String

    override fun equals(other: Any?) = this::class.safeCast(other)?.let {
        this.value == it.value
    } ?: false

    override fun hashCode() = value.hashCode()

    override fun toString() = value.toString()

    class StringValue(
        value: String,
        @Transient @StringRes val stringResId: Int? = null,
        @Transient private val formatArgs: Array<Any>? = null,
    ) : Value<String>(value) {
        override val valueSerializer = String.serializer()

        override fun getDisplayValue(context: Context) = stringResId?.let {
            formatArgs?.let { formatArgs ->
                context.getString(it, *formatArgs)
            } ?: context.getString(it)
        } ?: value
    }

    class BooleanValue(
        value: Boolean,
    ) : Value<Boolean>(value) {
        override val valueSerializer = Boolean.serializer()

        override fun getDisplayValue(context: Context) = context.getString(
            when (value) {
                true -> R.string.yes
                false -> R.string.no
            }
        )
    }

    class DateValue(
        value: Date,
    ) : Value<Date>(value) {
        override val valueSerializer = Companion.valueSerializer

        override fun getDisplayValue(context: Context): String = dateFormatter.format(value)

        companion object {
            private val valueSerializer = object : KSerializer<Date> {
                override val descriptor = PrimitiveSerialDescriptor("Date", PrimitiveKind.LONG)
                override fun serialize(encoder: Encoder, value: Date) = encoder.encodeLong(value.time)
                override fun deserialize(decoder: Decoder): Date = Date(decoder.decodeLong())
            }

            private val dateFormatter = SimpleDateFormat("yyyy/MM/dd hh:mm:ss", Locale.US)
        }
    }

    class ByteValue(
        value: Byte,
        @Transient private val valueToStringResId: Map<Byte, Int>? = null,
    ) : NumberValue<Byte>(value, valueToStringResId) {
        override val valueSerializer = Byte.serializer()
    }

    class DoubleValue(
        value: Double,
        @Transient private val valueToStringResId: Map<Double, Int>? = null,
    ) : NumberValue<Double>(value, valueToStringResId) {
        override val valueSerializer = Double.serializer()
    }

    class FloatValue(
        value: Float,
        @Transient private val valueToStringResId: Map<Float, Int>? = null,
    ) : NumberValue<Float>(value, valueToStringResId) {
        override val valueSerializer = Float.serializer()
    }

    class IntValue(
        value: Int,
        @Transient private val valueToStringResId: Map<Int, Int>? = null,
    ) : NumberValue<Int>(value, valueToStringResId) {
        override val valueSerializer = Int.serializer()
    }

    class LongValue(
        value: Long,
        @Transient private val valueToStringResId: Map<Long, Int>? = null,
    ) : NumberValue<Long>(value, valueToStringResId) {
        override val valueSerializer = Long.serializer()
    }

    class ShortValue(
        value: Short,
        @Transient private val valueToStringResId: Map<Short, Int>? = null,
    ) : NumberValue<Short>(value, valueToStringResId) {
        override val valueSerializer = Short.serializer()
    }

    class UByteValue(
        value: UByte,
        @Transient private val valueToStringResId: Map<UByte, Int>? = null,
    ) : NumberValue<UByte>(value, valueToStringResId) {
        override val valueSerializer = UByte.serializer()
    }

    class UIntValue(
        value: UInt,
        @Transient private val valueToStringResId: Map<UInt, Int>? = null,
    ) : NumberValue<UInt>(value, valueToStringResId) {
        override val valueSerializer = UInt.serializer()
    }

    class ULongValue(
        value: ULong,
        @Transient private val valueToStringResId: Map<ULong, Int>? = null,
    ) : NumberValue<ULong>(value, valueToStringResId) {
        override val valueSerializer = ULong.serializer()
    }

    class UShortValue(
        value: UShort,
        @Transient private val valueToStringResId: Map<UShort, Int>? = null,
    ) : NumberValue<UShort>(value, valueToStringResId) {
        override val valueSerializer = UShort.serializer()
    }

    class IntArrayValue(
        value: Array<Int>,
        @Transient private val valueToStringResId: Map<Int, Int>? = null,
    ) : ArrayValue<Int>(value, valueToStringResId, R.string.unknown_value_number) {
        override val elementKClass = Int::class
        override val elementSerializer = Int.serializer()
    }

    class FloatArrayValue(value: Array<Float>) : ArrayValue<Float>(value) {
        override val elementKClass = Float::class
        override val elementSerializer = Float.serializer()
    }

    class StringArrayValue(value: Array<String>) : ArrayValue<String>(value) {
        override val elementKClass = String::class
        override val elementSerializer = String.serializer()
    }

    class BytesValue(
        value: Long,
    ) : Value<Long>(value) {
        override val valueSerializer = Long.serializer()

        override fun getDisplayValue(context: Context) =
            "${
                BytesUtils.toHumanReadableSIPrefixes(value)
            } (${
                BytesUtils.toHumanReadableBinaryPrefixes(value)
            })"
    }

    class EnumValue<T : Enum<T>>(
        value: T,
        override val valueSerializer: KSerializer<T>,
        private val valueToStringResId: Map<T, Int>? = null,
    ) : Value<T>(value) {
        override fun getDisplayValue(
            context: Context
        ) = valueToStringResId?.let { valueToStringResId ->
            valueToStringResId[value]?.let {
                context.getString(it)
            } ?: context.getString(R.string.unknown_value_enum, value.name, value.ordinal)
        } ?: value.name
    }

    class FrequencyValue(
        valueHz: Long,
    ) : Value<Long>(valueHz) {
        override val valueSerializer = Long.serializer()

        override fun getDisplayValue(context: Context) = FrequencyUtils.toHumanReadable(value)
    }

    abstract class NumberValue<T>(
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
        abstract val elementKClass: KClass<T>

        abstract val elementSerializer: KSerializer<T>

        @OptIn(ExperimentalSerializationApi::class)
        override val valueSerializer by lazy { ArraySerializer(elementKClass, elementSerializer) }

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

        override fun equals(other: Any?) = ArrayValue::class.safeCast(other)?.let {
            value.contentEquals(it.value)
        } ?: false

        override fun hashCode() = value.contentHashCode()
    }

    companion object {
        class Serializer<T>(private val valueSerializer: KSerializer<T>) : KSerializer<Value<T>> {
            override val descriptor = valueSerializer.descriptor

            override fun deserialize(decoder: Decoder): Value<T> {
                error("Deserialization is unsupported")
            }

            override fun serialize(encoder: Encoder, value: Value<T>) {
                valueSerializer.serialize(encoder, value.value)
            }
        }

        operator fun invoke(
            value: String,
            @StringRes stringResId: Int? = null,
            formatArgs: Array<Any>? = null,
        ) = StringValue(value, stringResId, formatArgs)

        operator fun invoke(value: Boolean) = BooleanValue(value)

        operator fun invoke(value: Date) = DateValue(value)

        operator fun invoke(
            value: Byte,
            valueToStringResId: Map<Byte, Int>? = null,
        ) = ByteValue(value, valueToStringResId)

        operator fun invoke(
            value: Double,
            valueToStringResId: Map<Double, Int>? = null,
        ) = DoubleValue(value, valueToStringResId)

        operator fun invoke(
            value: Float,
            valueToStringResId: Map<Float, Int>? = null,
        ) = FloatValue(value, valueToStringResId)

        operator fun invoke(
            value: Int,
            valueToStringResId: Map<Int, Int>? = null,
        ) = IntValue(value, valueToStringResId)

        operator fun invoke(
            value: Long,
            valueToStringResId: Map<Long, Int>? = null,
        ) = LongValue(value, valueToStringResId)

        operator fun invoke(
            value: Short,
            valueToStringResId: Map<Short, Int>? = null,
        ) = ShortValue(value, valueToStringResId)

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
        ) = EnumValue(value, serializer<T>(), valueToStringResId)
    }
}
