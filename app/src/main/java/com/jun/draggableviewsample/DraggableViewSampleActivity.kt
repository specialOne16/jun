package com.jun.draggableviewsample

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.jun.view.DraggableView

class DraggableViewSampleActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coba_draggable_view)

    }

    override fun onResume() {
        super.onResume()
        val draggable = findViewById<DraggableView>(R.id.draggable)

        findViewById<View>(R.id.drag_area).setOnClickListener {
            Toast.makeText(this, "Showing click", Toast.LENGTH_SHORT).show()
        }

        draggable.debugPosition = true
        draggable.useParentBoundaries = true
        draggable.dragArea = findViewById(R.id.drag_area)
    }
}