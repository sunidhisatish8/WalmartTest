package com.example.walmart.presentation.countries

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class CountriesItemDecoration(private val spacing: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val itemPosition = parent.getChildAdapterPosition(view)
        val itemCount = parent.adapter?.itemCount ?: RecyclerView.NO_POSITION

        // add bottom spacing only for the latest item
        val bottomSpacing = if (itemPosition == itemCount - 1) {
            spacing
        } else {
            0
        }

        outRect.set(spacing, spacing, spacing, bottomSpacing)
    }
}