package com.mobile.wanda.promoter.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.View

import com.mobile.wanda.promoter.R

/**
 * Created by kombo on 25/03/2018.
 */

class DividerItemDecoration : RecyclerView.ItemDecoration {

    private var mDivider: Drawable? = null

    constructor(context: Context) {
        mDivider = ContextCompat.getDrawable(context, R.drawable.divider)
    }

    constructor(context: Context, resource: Int) {
        mDivider = ContextCompat.getDrawable(context, resource)
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State?) {
        val left = parent.paddingLeft
        val right = parent.width - parent.paddingRight

        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)

            val params = child.layoutParams as RecyclerView.LayoutParams

            val top = child.bottom + params.bottomMargin
            val bottom = top + mDivider!!.intrinsicHeight

            mDivider!!.setBounds(left, top, right, bottom)
            mDivider!!.draw(c)
        }
    }
}
