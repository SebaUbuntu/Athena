/*
 * SPDX-FileCopyrightText: 2023 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.ui.views

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.graphics.drawable.Icon
import android.net.Uri
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.view.isVisible
import dev.sebaubuntu.athena.R

/**
 * A poor man's M3 ListItem implementation
 * https://m3.material.io/components/lists/overview
 */
class ListItem @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {
    private val headlineTextView by lazy { findViewById<TextView>(R.id.headlineTextView) }
    private val leadingIconImageView by lazy { findViewById<ImageView>(R.id.leadingIconImageView) }
    private val supportingTextView by lazy { findViewById<TextView>(R.id.supportingTextView) }
    private val trailingIconImageView by lazy { findViewById<ImageView>(R.id.trailingIconImageView) }
    private val trailingSupportingTextView by lazy { findViewById<TextView>(R.id.trailingSupportingTextView) }

    var headlineText: CharSequence?
        get() = headlineTextView.text
        set(value) {
            headlineTextView.setTextAndUpdateVisibility(value)
        }

    var leadingIconImage: Drawable?
        get() = leadingIconImageView.drawable
        set(value) {
            leadingIconImageView.setImageAndUpdateVisibility(value)
        }

    var supportingText: CharSequence?
        get() = supportingTextView.text
        set(value) {
            supportingTextView.setTextAndUpdateVisibility(value)
        }

    var trailingIconImage: Drawable?
        get() = trailingIconImageView.drawable
        set(value) {
            trailingIconImageView.setImageAndUpdateVisibility(value)
        }

    var trailingSupportingText: CharSequence?
        get() = trailingSupportingTextView.text
        set(value) {
            trailingSupportingTextView.setTextAndUpdateVisibility(value)
        }

    init {
        inflate(context, R.layout.list_item, this)

        context.obtainStyledAttributes(attrs, R.styleable.ListItem, 0, 0).apply {
            try {
                headlineText = getString(R.styleable.ListItem_headlineText)
                leadingIconImage = getDrawable(R.styleable.ListItem_leadingIconImage)
                supportingText = getString(R.styleable.ListItem_supportingText)
                trailingIconImage = getDrawable(R.styleable.ListItem_trailingIconImage)
                trailingSupportingText = getString(R.styleable.ListItem_trailingSupportingText)
            } finally {
                recycle()
            }
        }
    }

    fun setHeadlineText(@StringRes resId: Int) = headlineTextView.setTextAndUpdateVisibility(resId)
    fun setHeadlineText(@StringRes resId: Int, vararg formatArgs: Any) =
        headlineTextView.setTextAndUpdateVisibility(resId, *formatArgs)

    fun setLeadingIconImage(bm: Bitmap) = leadingIconImageView.setImageAndUpdateVisibility(bm)
    fun setLeadingIconImage(icon: Icon) = leadingIconImageView.setImageAndUpdateVisibility(icon)
    fun setLeadingIconImage(@DrawableRes resId: Int) =
        leadingIconImageView.setImageAndUpdateVisibility(resId)

    fun setLeadingIconImage(uri: Uri) = leadingIconImageView.setImageAndUpdateVisibility(uri)

    fun setSupportingText(@StringRes resId: Int) =
        supportingTextView.setTextAndUpdateVisibility(resId)

    fun setSupportingText(@StringRes resId: Int, vararg formatArgs: Any) =
        supportingTextView.setTextAndUpdateVisibility(resId, *formatArgs)

    fun setTrailingIconImage(bm: Bitmap) = trailingIconImageView.setImageAndUpdateVisibility(bm)
    fun setTrailingIconImage(icon: Icon) = trailingIconImageView.setImageAndUpdateVisibility(icon)
    fun setTrailingIconImage(@DrawableRes resId: Int) =
        trailingIconImageView.setImageAndUpdateVisibility(resId)

    fun setTrailingIconImage(uri: Uri) = trailingIconImageView.setImageAndUpdateVisibility(uri)

    fun setTrailingSupportingText(@StringRes resId: Int) =
        trailingSupportingTextView.setTextAndUpdateVisibility(resId)

    fun setTrailingSupportingText(@StringRes resId: Int, vararg formatArgs: Any) =
        trailingSupportingTextView.setTextAndUpdateVisibility(resId, *formatArgs)

    // ImageView utils

    private fun ImageView.setImageAndUpdateVisibility(bm: Bitmap) {
        setImageBitmap(bm)
        isVisible = true
    }

    private fun ImageView.setImageAndUpdateVisibility(drawable: Drawable?) {
        setImageDrawable(drawable)
        isVisible = drawable != null
    }

    private fun ImageView.setImageAndUpdateVisibility(icon: Icon) {
        setImageIcon(icon)
        isVisible = true
    }

    private fun ImageView.setImageAndUpdateVisibility(@DrawableRes resId: Int) {
        setImageResource(resId)
        isVisible = true
    }

    private fun ImageView.setImageAndUpdateVisibility(uri: Uri) {
        setImageURI(uri)
        isVisible = true
    }

    // TextView utils

    private fun TextView.setTextAndUpdateVisibility(text: CharSequence?) {
        this.text = text.also {
            isVisible = !it.isNullOrEmpty()
        }
    }

    private fun TextView.setTextAndUpdateVisibility(@StringRes resId: Int) =
        setTextAndUpdateVisibility(resources.getText(resId))

    private fun TextView.setTextAndUpdateVisibility(@StringRes resId: Int, vararg formatArgs: Any) =
        setTextAndUpdateVisibility(resources.getString(resId, *formatArgs))
}
