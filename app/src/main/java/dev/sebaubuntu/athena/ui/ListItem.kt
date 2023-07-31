/*
 * SPDX-FileCopyrightText: 2023 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.athena.ui

import android.content.Context
import android.util.AttributeSet
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.google.android.material.divider.MaterialDivider
import dev.sebaubuntu.athena.R

/**
 * A poor man's M3 ListItem implementation
 * https://m3.material.io/components/lists/overview
 */
class ListItem @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs) {
    private val divider by lazy { findViewById<MaterialDivider>(R.id.divider) }
    private val headlineTextView by lazy { findViewById<TextView>(R.id.headlineTextView) }
    private val supportingTextView by lazy { findViewById<TextView>(R.id.supportingTextView) }
    private val trailingSupportingTextView by lazy { findViewById<TextView>(R.id.trailingSupportingTextView) }

    var headlineText: CharSequence?
        get() = headlineTextView.text
        set(value) {
            headlineTextView.text = value
            headlineTextView.isVisible = !headlineText.isNullOrEmpty()
        }
    var supportingText: CharSequence?
        get() = supportingTextView.text
        set(value) {
            supportingTextView.text = value
            supportingTextView.isVisible = !headlineText.isNullOrEmpty()
        }
    var trailingSupportingText: CharSequence?
        get() = trailingSupportingTextView.text
        set(value) {
            trailingSupportingTextView.text = value
            trailingSupportingTextView.isVisible = !headlineText.isNullOrEmpty()
        }
    var showDivider: Boolean = true
        set(value) {
            field = value
            divider.isVisible = value
        }

    init {
        inflate(context, R.layout.list_item, this)

        context.obtainStyledAttributes(attrs, R.styleable.ListItem, 0, 0).apply {
            try {
                headlineText = getString(R.styleable.ListItem_headlineText)
                supportingText = getString(R.styleable.ListItem_supportingText)
                trailingSupportingText = getString(R.styleable.ListItem_trailingSupportingText)
                showDivider = getBoolean(R.styleable.ListItem_showDivider, true)
            } finally {
                recycle()
            }
        }
    }
}
