/*
 * SPDX-FileCopyrightText: 2023 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.ui

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
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
            headlineTextView.text = value
            headlineTextView.isVisible = !value.isNullOrEmpty()
        }
    var leadingIconImage: Drawable?
        get() = leadingIconImageView.drawable
        set(value) {
            leadingIconImageView.setImageDrawable(value)
            leadingIconImageView.isVisible = value != null
        }
    var supportingText: CharSequence?
        get() = supportingTextView.text
        set(value) {
            supportingTextView.text = value
            supportingTextView.isVisible = !value.isNullOrEmpty()
        }
    var trailingIconImage: Drawable?
        get() = trailingIconImageView.drawable
        set(value) {
            trailingIconImageView.setImageDrawable(value)
            trailingIconImageView.isVisible = value != null
        }
    var trailingSupportingText: CharSequence?
        get() = trailingSupportingTextView.text
        set(value) {
            trailingSupportingTextView.text = value
            trailingSupportingTextView.isVisible = !value.isNullOrEmpty()
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
}
