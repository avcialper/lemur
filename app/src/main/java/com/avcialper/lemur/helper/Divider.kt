package com.avcialper.lemur.helper

import android.content.Context
import com.google.android.material.divider.MaterialDividerItemDecoration

class Divider(context: Context) : MaterialDividerItemDecoration(context, VERTICAL) {

    init {
        isLastItemDecorated = false
    }

}