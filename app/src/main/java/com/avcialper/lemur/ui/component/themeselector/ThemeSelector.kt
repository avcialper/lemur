package com.avcialper.lemur.ui.component.themeselector

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.avcialper.lemur.databinding.FragmentThemeSelectorBinding
import com.avcialper.lemur.util.constant.Theme
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ThemeSelector : BottomSheetDialogFragment() {

    private var _binding: FragmentThemeSelectorBinding? = null
    private val binding get() = _binding!!

    private val vm: ThemeSelectorViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentThemeSelectorBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeTheme()
        initUI()
    }

    private fun initUI() = with(binding) {
        themeGroup.setOnCheckedChangeListener { _, checkedId ->
            val theme = when (checkedId) {
                light.id -> Theme.LIGHT
                dark.id -> Theme.DARK
                else -> Theme.SYSTEM_DEFAULT
            }
            vm.changeTheme(theme)
        }
    }

    private fun observeTheme() = with(binding) {
        viewLifecycleOwner.lifecycleScope.launch {
            vm.theme.collect {
                val theme = when (it) {
                    Theme.LIGHT -> light.id
                    Theme.DARK -> dark.id
                    Theme.SYSTEM_DEFAULT -> systemDefault.id
                }
                themeGroup.check(theme)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}