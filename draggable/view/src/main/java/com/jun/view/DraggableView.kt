package com.jun.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.core.view.children
import com.jun.view.DraggableView.SnapDirection.BOTH
import com.jun.view.DraggableView.SnapDirection.HORIZONTAL
import com.jun.view.DraggableView.SnapDirection.VERTICAL

class DraggableView(context: Context, attributeSet: AttributeSet? = null) :
    RelativeLayout(context, attributeSet) {

    var snapDirection: SnapDirection? = null
    var useParentBoundaries = true
    var debugPosition = false
        set(value) {
            if (value) {
                displayBounds()
            }
            field = value
        }
    var dragArea: View? = null
        set(value) {
            if (children.contains(value)) {
                field = value
            } else {
                throw IllegalArgumentException("Drag area must be child of this DraggableView")
            }
        }


    private val clickDispatcher: ClickDispatcher by lazy { provideClickDispatcher(this) }

    private var initialViewX = 0f
    private var initialViewY = 0f
    private var initialTouchX = 0f
    private var initialTouchY = 0f
    private var dragStarted = false

    private val xBarrier by lazy { (containerWidth() - width).toFloat() }
    private val yBarrier by lazy { (containerHeight() - height).toFloat() }

    override fun onViewAdded(child: View?) {
        super.onViewAdded(child)
        child?.isClickable = false
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        ensureChildNotClickable()
        return super.dispatchTouchEvent(ev)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                clickDispatcher.actionDown(event)

                initialViewX = x
                initialViewY = y

                initialTouchX = event.rawX
                initialTouchY = event.rawY

                dragStarted = dragArea?.let { isChildTouched(it, event) } ?: true
            }

            MotionEvent.ACTION_MOVE -> {
                if (dragStarted) {
                    val currentTouchX = event.rawX
                    val currentTouchY = event.rawY
                    val targetX = initialViewX + (currentTouchX - initialTouchX)
                    val targetY = initialViewY + (currentTouchY - initialTouchY)

                    with(animate()) {
                        duration = 0

                        if (useParentBoundaries) {
                            x(targetX.coerceIn(0f, xBarrier))
                            y(targetY.coerceIn(0f, yBarrier))
                        } else {
                            x(targetX)
                            y(targetY)
                        }
                        start()
                    }
                }
            }

            MotionEvent.ACTION_UP -> {
                clickDispatcher.actionUp(event)

                if (snapDirection != null && dragStarted) {
                    val currentTouchX = event.rawX
                    val currentTouchY = event.rawY

                    val viewCenterX = x + width / 2
                    val viewCenterY = y + height / 2
                    val containerCenterX = containerWidth() / 2
                    val containerCenterY = containerHeight() / 2
                    var targetX = initialViewX + (currentTouchX - initialTouchX)
                    var targetY = initialViewY + (currentTouchY - initialTouchY)

                    if (snapDirection in listOf(HORIZONTAL, BOTH)) {
                        targetX = if (viewCenterX <= containerCenterX) {
                            0f // Move to left
                        } else {
                            xBarrier // Move to right
                        }
                    }
                    if (snapDirection in listOf(VERTICAL, BOTH)) {
                        targetY = if (viewCenterY <= containerCenterY) {
                            0f // Move to top
                        } else {
                            yBarrier // Move to bottom
                        }
                    }

                    animate()
                        .x(targetX)
                        .y(targetY)
                        .setDuration(300)
                        .start()
                    dragStarted = false
                }
            }
        }
        if (debugPosition) displayBounds()

        return true
    }

    private fun containerHeight() = (parent as? ViewGroup)?.height
        ?: throw IllegalStateException("DraggableView must have ViewGroup as parent")

    private fun containerWidth() = (parent as? ViewGroup)?.width
        ?: throw IllegalStateException("DraggableView must have ViewGroup as parent")

    private fun ensureChildNotClickable() = children.forEach { it.isClickable = false }

    enum class SnapDirection {
        HORIZONTAL, VERTICAL, BOTH
    }
}

