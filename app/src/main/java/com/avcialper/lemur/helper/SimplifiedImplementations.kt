package com.avcialper.lemur.helper

import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener

interface SimplifiedAnimationListener : AnimationListener {
    override fun onAnimationStart(animation: Animation?) {}
    override fun onAnimationEnd(animation: Animation?) {}
    override fun onAnimationRepeat(animation: Animation?) {}
}