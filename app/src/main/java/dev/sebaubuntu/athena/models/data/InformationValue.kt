/*
 * SPDX-FileCopyrightText: 2024 Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.models.data

import android.content.Context
import androidx.annotation.StringRes
import dev.sebaubuntu.athena.R
import dev.sebaubuntu.athena.utils.serializer.DateAsLongSerializer
import dev.sebaubuntu.athena.ext.stringRes
import dev.sebaubuntu.athena.utils.BytesUtils
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.builtins.ArraySerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.reflect.KClass
import kotlin.reflect.safeCast

@Serializable(with = InformationValue.Companion.Serializer::class)
sealed class InformationValue {
    abstract val value: Any
    abstract val valueSerializer: KSerializer<*>
    abstract fun getDisplayValue(context: Context): String

    data class StringValue(
        override val value: String,
        @Transient @StringRes val resId: Int? = null,
        @Transient private val formatArgs: Array<Any>? = null,
    ) : InformationValue() {
        override val valueSerializer = String.serializer()
        override fun getDisplayValue(context: Context) = resId?.let {
            formatArgs?.let { formatArgs ->
                context.getString(it, *formatArgs)
            } ?: context.getString(it)
        } ?: value

        override fun equals(other: Any?) = StringValue::class.safeCast(other)?.let { o ->
            value == o.value
                    && resId == o.resId
                    && formatArgs?.let {
                o.formatArgs?.let { oFormatArgs ->
                    it.contentEquals(oFormatArgs)
                } ?: false
            } ?: (o.formatArgs == null)
        } ?: false

        override fun hashCode(): Int {
            var result = value.hashCode()
            result = 31 * result + (resId ?: 0)
            result = 31 * result + (formatArgs?.contentHashCode() ?: 0)
            return result
        }
    }

    data class BooleanValue(
        override val value: Boolean,
    ) : InformationValue() {
        override val valueSerializer = Boolean.serializer()
        override fun getDisplayValue(context: Context) = context.getString(value.stringRes)
    }

    data class DateValue(
        override val value: Date,
    ) : InformationValue() {
        override val valueSerializer = DateAsLongSerializer
        override fun getDisplayValue(context: Context): String = dateFormatter.format(value)

        companion object {
            private val dateFormatter = SimpleDateFormat("yyyy/MM/dd hh:mm:ss", Locale.US)
        }
    }

    data class IntValue(
        override val value: Int,
        @Transient private val valueToStringResId: Map<Int, Int>? = null,
    ) : NumberValue<Int>(valueToStringResId, R.string.unknown_value_int)  {
        override val valueSerializer = Int.serializer()
    }

    data class LongValue(
        override val value: Long,
        @Transient private val valueToStringResId: Map<Long, Int>? = null,
    ) : NumberValue<Long>(valueToStringResId, R.string.unknown_value_int) {
        override val valueSerializer = Long.serializer()
    }

    class IntArrayValue(
        override val value: Array<Int>,
        @Transient private val valueToStringResId: Map<Int, Int>? = null,
    ) : ArrayValue<Int>(valueToStringResId, R.string.unknown_value_int) {
        override val elementKClass = Int::class
        override val elementSerializer = Int.serializer()
    }

    class FloatArrayValue(override val value: Array<Float>) : ArrayValue<Float>() {
        override val elementKClass = Float::class
        override val elementSerializer = Float.serializer()
    }

    class StringArrayValue(override val value: Array<String>) : ArrayValue<String>() {
        override val elementKClass = String::class
        override val elementSerializer = String.serializer()
    }

    data class BytesValue(
        override val value: Long,
    ) : InformationValue() {
        override val valueSerializer = Long.serializer()
        override fun getDisplayValue(context: Context) =
            "${
                BytesUtils.toHumanReadableSIPrefixes(value)
            } (${
                BytesUtils.toHumanReadableBinaryPrefixes(value)
            })"
    }

    class EnumValue<T : Enum<T>>(
        private val enum: T,
        private val valueToStringResId: Map<T, Int>? = null,
    ) : InformationValue() {
        override val value = enum.name
        override val valueSerializer = String.serializer()
        override fun getDisplayValue(
            context: Context
        ) = valueToStringResId?.let { valueToStringResId ->
            valueToStringResId[enum]?.let {
                context.getString(it)
            } ?: context.getString(R.string.unknown_value_enum, enum.name, enum.ordinal)
        } ?: enum.name
    }

    abstract class NumberValue<T : Number>(
        @Transient private val valueToStringResId: Map<T, Int>? = null,
        @Transient @StringRes private val unknownValueStringResId: Int? = null,
    ) : InformationValue() {
        abstract override val value: T
        abstract override val valueSerializer: KSerializer<T>
        override fun getDisplayValue(
            context: Context
        ) = valueToStringResId?.let { valueToStringResId ->
            valueToStringResId[value]?.let {
                context.getString(it)
            } ?: unknownValueStringResId?.let {
                context.getString(it, value)
            }
        } ?: value.toString()
    }

    abstract class ArrayValue<T : Any>(
        @Transient private val valueToStringResId: Map<T, Int>? = null,
        @Transient @StringRes private val unknownValueStringResId: Int? = null,
    ) : InformationValue() {
        abstract val elementKClass: KClass<T>
        abstract val elementSerializer: KSerializer<T>

        abstract override val value: Array<T>
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

        override fun equals(other: Any?) = ArrayValue::class.safeCast(other)?.let { o ->
            value.contentEquals(o.value)
        } ?: false

        override fun hashCode() = value.contentHashCode()
    }

    companion object {
        object Serializer : KSerializer<InformationValue> {
            override val descriptor = String.serializer().descriptor

            override fun deserialize(decoder: Decoder): InformationValue {
                throw Exception("Deserialization is unsupported")
            }

            override fun serialize(encoder: Encoder, value: InformationValue) {
                (value.valueSerializer as KSerializer<Any>).serialize(encoder, value.value)
            }
        }
    }
}
