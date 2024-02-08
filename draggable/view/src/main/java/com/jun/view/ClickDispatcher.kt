package com.jun.view

import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import kotlin.math.sqrt
import kotlin.time.Duration.Companion.milliseconds

interface ClickDispatcher {
    fun actionDown(event: MotionEvent)
    fun actionUp(event: MotionEvent)
}

internal fun provideClickDispatcher(view: View): ClickDispatcher {
    return ClickDispatcherImpl(view)
}

private class ClickDispatcherImpl(private val view: View) : ClickDispatcher {

    private var initX: Float? = null
    private var initY: Float? = null
    private var time: Long? = null

    override fun actionDown(event: MotionEvent) {
        initX = event.rawX
        initY = event.rawY
        time = System.currentTimeMillis()
    }

    override fun actionUp(event: MotionEvent) {
        val x1 = initX
        val y1 = initY
        val lastTime = time?.milliseconds

        if (x1 == null || y1 == null || lastTime == null) {
            throw IllegalStateException("actionDown not called yet")
        } else {
            val currentTime = System.currentTimeMillis().milliseconds

            val inClickDistance = distance(x1, y1, event.rawX, event.rawY) < 10f
            val inClickDuration = currentTime - lastTime < 500.milliseconds

            if (inClickDistance && inClickDuration) {
                performClick(event)
            }
        }

        initX = null
        initY = null
        time = null
    }

    private fun performClick(event: MotionEvent) {
        if (view is ViewGroup) {
            val touchedChildren = view.findChildrenTouchPosition(event.x, event.y)

            // By default children sorted from back to front,
            // but we want performClick triggered from from to back
            for (child in touchedChildren.asIterable().reversed()) {
                if (child.performClick()) return
            }
        }
        view.performClick()
    }

    private fun distance(x1: Float, y1: Float, x2: Float, y2: Float): Float {
        return sqrt(((x1 - x2) * (x1 - x2)) + ((y1 - y2) * (y1 - y2)))
    }
}
