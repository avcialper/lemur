package com.avcialper.lemur.helper

import android.text.Editable
import android.text.TextWatcher
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener

interface SimplifiedAnimationListener : AnimationListener {
    override fun onAnimationStart(animation: Animation?) {}
    override fun onAnimationEnd(animation: Animation?) {}
    override fun onAnimationRepeat(animation: Animation?) {}
}

interface SimplifiedTextWatcher : TextWatcher {
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    override fun afterTextChanged(s: Editable?) {}
}