package com.jun.view

import android.content.Context
import android.graphics.Color
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.view.children


internal fun ViewGroup.findChildrenTouchPosition(x: Float, y: Float) = children.filter {
    val inXRange = x > it.x && x < it.x + it.width
    val inYRange = y > it.y && y < it.y + it.height
    inXRange && inYRange
}

internal fun View.isChildTouched(child: View, event: MotionEvent): Boolean {
    if (this !is ViewGroup) return false

    val inXRange = event.x > child.x && event.x < child.x + child.width
    val inYRange = event.y > child.y && event.y < child.y + child.height
    return inXRange && inYRange
}

fun RelativeLayout.displayBounds() {
    fun wrapContentInRelativeLayout() = RelativeLayout.LayoutParams(
        LinearLayout.LayoutParams.WRAP_CONTENT,
        LinearLayout.LayoutParams.WRAP_CONTENT
    )

    fun textView(context: Context, text: String): TextView = TextView(context).apply {
        this.text = text
        setTextColor(Color.parseColor("#ffffff"))
        setBackgroundColor(Color.parseColor("#000000"))
    }

    if (children.count() >= 4) removeViews(0, 4)

    val topStart = x to y
    val topEnd = x + width to y
    val bottomEnd = x + width to y + height
    val bottomStart = x to y + height

    addView(
        /* child = */ textView(context, "x: ${topStart.first}\ny: ${topStart.second}"),
        /* index = */ 0,
        /* params = */ wrapContentInRelativeLayout().apply {
            addRule(RelativeLayout.ALIGN_PARENT_TOP)
            addRule(RelativeLayout.ALIGN_PARENT_START)
        }
    )

    addView(
        /* child = */ textView(context, "x: ${topEnd.first}\ny: ${topEnd.second}"),
        /* index = */ 0,
        /* params = */ wrapContentInRelativeLayout().apply {
            addRule(RelativeLayout.ALIGN_PARENT_TOP)
            addRule(RelativeLayout.ALIGN_PARENT_END)
        }
    )

    addView(
        /* child = */ textView(context, "x: ${bottomEnd.first}\ny: ${bottomEnd.second}"),
        /* index = */ 0,
        /* params = */ wrapContentInRelativeLayout().apply {
            addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
            addRule(RelativeLayout.ALIGN_PARENT_END)
        }
    )

    addView(
        /* child = */ textView(context, "x: ${bottomStart.first}\ny: ${bottomStart.second}"),
        /* index = */ 0,
        /* params = */ wrapContentInRelativeLayout().apply {
            addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
            addRule(RelativeLayout.ALIGN_PARENT_START)
        }
    )
}
