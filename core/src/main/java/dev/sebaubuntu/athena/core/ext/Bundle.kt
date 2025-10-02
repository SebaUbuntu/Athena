/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.core.ext

import android.os.Bundle
import android.os.IBinder
import android.os.Parcelable
import android.util.Size
import android.util.SizeF
import android.util.SparseArray
import androidx.core.os.BundleCompat
import java.io.Serializable

fun <T> Bundle.getOrDefault(
    key: String,
    defaultValue: T,
    getter: Bundle.(String) -> T,
): T = when (containsKey(key)) {
    true -> getter(key)
    false -> defaultValue
}

/**
 * @see Bundle.getBinder
 */
fun Bundle.getBinderOrDefault(
    key: String,
    defaultValue: IBinder? = null,
): IBinder? = getOrDefault(key, defaultValue, Bundle::getBinder)

/**
 * @see Bundle.getBoolean
 */
fun Bundle.getBooleanOrDefault(
    key: String,
    defaultValue: Boolean,
): Boolean = getOrDefault(key, defaultValue, Bundle::getBoolean)

/**
 * @see Bundle.getBoolean
 */
fun Bundle.getBooleanOrNull(
    key: String,
): Boolean? = getOrDefault(key, null, Bundle::getBoolean)

/**
 * @see Bundle.getBooleanArray
 */
fun Bundle.getBooleanArrayOrDefault(
    key: String,
    defaultValue: BooleanArray? = null,
): BooleanArray? = getOrDefault(key, defaultValue, Bundle::getBooleanArray)

/**
 * @see Bundle.getByte
 */
fun Bundle.getByteOrDefault(
    key: String,
    defaultValue: Byte,
): Byte = getOrDefault(key, defaultValue, Bundle::getByte)

/**
 * @see Bundle.getByte
 */
fun Bundle.getByteOrNull(
    key: String,
): Byte? = getOrDefault(key, null, Bundle::getByte)

/**
 * @see Bundle.getByteArray
 */
fun Bundle.getByteArrayOrDefault(
    key: String,
    defaultValue: ByteArray? = null,
): ByteArray? = getOrDefault(key, defaultValue, Bundle::getByteArray)

/**
 * @see Bundle.getBundle
 */
fun Bundle.getBundleOrDefault(
    key: String,
    defaultValue: Bundle? = null,
): Bundle? = getOrDefault(key, defaultValue, Bundle::getBundle)

/**
 * @see Bundle.getChar
 */
fun Bundle.getCharOrDefault(
    key: String,
    defaultValue: Char,
): Char = getOrDefault(key, defaultValue, Bundle::getChar)

/**
 * @see Bundle.getChar
 */
fun Bundle.getCharOrNull(
    key: String,
): Char? = getOrDefault(key, null, Bundle::getChar)

/**
 * @see Bundle.getCharArray
 */
fun Bundle.getCharArrayOrDefault(
    key: String,
    defaultValue: CharArray? = null,
): CharArray? = getOrDefault(key, defaultValue, Bundle::getCharArray)

/**
 * @see Bundle.getCharSequence
 */
fun Bundle.getCharSequenceOrDefault(
    key: String,
    defaultValue: CharSequence? = null,
): CharSequence? = getOrDefault(key, defaultValue, Bundle::getCharSequence)

/**
 * @see Bundle.getCharSequenceArray
 */
fun Bundle.getCharSequenceArrayOrDefault(
    key: String,
    defaultValue: Array<CharSequence>? = null,
): Array<CharSequence>? = getOrDefault(key, defaultValue, Bundle::getCharSequenceArray)

/**
 * @see Bundle.getCharSequenceArrayList
 */
fun Bundle.getCharSequenceArrayListOrDefault(
    key: String,
    defaultValue: ArrayList<CharSequence>? = null,
): ArrayList<CharSequence>? = getOrDefault(key, defaultValue, Bundle::getCharSequenceArrayList)

/**
 * @see Bundle.getDouble
 */
fun Bundle.getDoubleOrDefault(
    key: String,
    defaultValue: Double,
): Double = getOrDefault(key, defaultValue, Bundle::getDouble)

/**
 * @see Bundle.getDouble
 */
fun Bundle.getDoubleOrNull(
    key: String,
): Double? = getOrDefault(key, null, Bundle::getDouble)

/**
 * @see Bundle.getDoubleArray
 */
fun Bundle.getDoubleArrayOrDefault(
    key: String,
    defaultValue: DoubleArray? = null,
): DoubleArray? = getOrDefault(key, defaultValue, Bundle::getDoubleArray)

/**
 * @see Bundle.getFloat
 */
fun Bundle.getFloatOrDefault(
    key: String,
    defaultValue: Float,
): Float = getOrDefault(key, defaultValue, Bundle::getFloat)

/**
 * @see Bundle.getFloat
 */
fun Bundle.getFloatOrNull(
    key: String,
): Float? = getOrDefault(key, null, Bundle::getFloat)

/**
 * @see Bundle.getFloatArray
 */
fun Bundle.getFloatArrayOrDefault(
    key: String,
    defaultValue: FloatArray? = null,
): FloatArray? = getOrDefault(key, defaultValue, Bundle::getFloatArray)

/**
 * @see Bundle.getInt
 */
fun Bundle.getIntOrDefault(
    key: String,
    defaultValue: Int,
): Int = getOrDefault(key, defaultValue, Bundle::getInt)

/**
 * @see Bundle.getInt
 */
fun Bundle.getIntOrNull(
    key: String,
) = getOrDefault(key, null, Bundle::getInt)

/**
 * @see Bundle.getIntArray
 */
fun Bundle.getIntArrayOrDefault(
    key: String,
    defaultValue: IntArray? = null,
): IntArray? = getOrDefault(key, defaultValue, Bundle::getIntArray)

/**
 * @see Bundle.getLong
 */
fun Bundle.getLongOrDefault(
    key: String,
    defaultValue: Long,
): Long = getOrDefault(key, defaultValue, Bundle::getLong)

/**
 * @see Bundle.getLong
 */
fun Bundle.getLongOrNull(
    key: String,
): Long? = getOrDefault(key, null, Bundle::getLong)

/**
 * @see Bundle.getLongArray
 */
fun Bundle.getLongArrayOrDefault(
    key: String,
    defaultValue: LongArray? = null,
): LongArray? = getOrDefault(key, defaultValue, Bundle::getLongArray)

/**
 * @see BundleCompat.getParcelable
 */
inline fun <reified T : Parcelable> Bundle.getParcelableOrDefault(
    key: String,
    defaultValue: T? = null,
): T? = getOrDefault(key, defaultValue) {
    BundleCompat.getParcelable(this, key, T::class.java)
}

/**
 * @see BundleCompat.getParcelableArray
 */
inline fun <reified T : Parcelable> Bundle.getParcelableArrayOrDefault(
    key: String,
    defaultValue: Array<Parcelable>? = null,
): Array<Parcelable>? = getOrDefault(key, defaultValue) {
    BundleCompat.getParcelableArray(this, key, T::class.java)
}

/**
 * @see BundleCompat.getParcelableArrayList
 */
inline fun <reified T : Parcelable> Bundle.getParcelableArrayListOrDefault(
    key: String,
    defaultValue: ArrayList<T>? = null,
): ArrayList<T>? = getOrDefault(key, defaultValue) {
    BundleCompat.getParcelableArrayList(this, key, T::class.java)
}

/**
 * @see BundleCompat.getSerializable
 */
inline fun <reified T : Serializable> Bundle.getSerializableOrDefault(
    key: String,
    defaultValue: T? = null,
): T? = getOrDefault(key, defaultValue) {
    BundleCompat.getSerializable(this, key, T::class.java)
}

/**
 * @see Bundle.getShort
 */
fun Bundle.getShortOrDefault(
    key: String,
    defaultValue: Short,
): Short = getOrDefault(key, defaultValue, Bundle::getShort)

/**
 * @see Bundle.getShort
 */
fun Bundle.getShortOrNull(
    key: String,
): Short? = getOrDefault(key, null, Bundle::getShort)

/**
 * @see Bundle.getShortArray
 */
fun Bundle.getShortArrayOrDefault(
    key: String,
    defaultValue: ShortArray? = null,
): ShortArray? = getOrDefault(key, defaultValue, Bundle::getShortArray)

/**
 * @see Bundle.getSize
 */
fun Bundle.getSizeOrDefault(
    key: String,
    defaultValue: Size? = null,
): Size? = getOrDefault(key, defaultValue, Bundle::getSize)

/**
 * @see Bundle.getSizeF
 */
fun Bundle.getSizeFOrDefault(
    key: String,
    defaultValue: SizeF? = null,
): SizeF? = getOrDefault(key, defaultValue, Bundle::getSizeF)

/**
 * @see BundleCompat.getSparseParcelableArray
 */
inline fun <reified T : Parcelable> Bundle.getSparseParcelableArrayOrDefault(
    key: String,
    defaultValue: SparseArray<Parcelable>? = null,
): SparseArray<Parcelable>? = getOrDefault(key, defaultValue) {
    BundleCompat.getSparseParcelableArray(this, key, T::class.java)
}

/**
 * @see Bundle.getString
 */
fun Bundle.getStringOrDefault(
    key: String,
    defaultValue: String? = null,
): String? = getOrDefault(key, defaultValue, Bundle::getString)

/**
 * @see Bundle.getStringArray
 */
fun Bundle.getStringArrayOrDefault(
    key: String,
    defaultValue: Array<String>? = null,
): Array<String>? = getOrDefault(key, defaultValue, Bundle::getStringArray)
