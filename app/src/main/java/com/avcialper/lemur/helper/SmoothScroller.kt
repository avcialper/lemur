package com.avcialper.lemur.helper

import android.content.Context
import android.util.DisplayMetrics
import androidx.recyclerview.widget.LinearSmoothScroller

class SmoothScroller(context: Context, targetPosition: Int) : LinearSmoothScroller(context) {

    init {
        this.targetPosition = targetPosition
    }

    override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float {
        return 100f / displayMetrics.densityDpi
    }
}