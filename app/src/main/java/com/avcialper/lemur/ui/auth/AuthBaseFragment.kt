package com.avcialper.lemur.ui.auth

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.avcialper.lemur.ui.BaseFragment

abstract class AuthBaseFragment<VB : ViewBinding>(
    inflate: (LayoutInflater, ViewGroup?, Boolean) -> VB
) : BaseFragment<VB>(inflate) {

    abstract fun validate(): Boolean

}