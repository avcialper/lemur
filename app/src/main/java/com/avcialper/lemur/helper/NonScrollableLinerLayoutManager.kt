package com.avcialper.lemur.helper

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager

class NonScrollableLinerLayoutManager(context: Context) :
    LinearLayoutManager(context, VERTICAL, false) {

    override fun canScrollVertically(): Boolean {
        return false
    }

}