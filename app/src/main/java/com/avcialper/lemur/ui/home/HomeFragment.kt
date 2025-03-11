package com.avcialper.lemur.ui.home

import com.avcialper.lemur.databinding.FragmentHomeBinding
import com.avcialper.lemur.ui.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {

    override fun FragmentHomeBinding.initialize() {
        initUI()
    }


    private fun initUI() = with(binding) {

    }

}