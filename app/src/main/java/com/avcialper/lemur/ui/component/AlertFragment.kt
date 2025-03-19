package com.avcialper.lemur.ui.component

import android.app.Dialog
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.avcialper.lemur.databinding.FragmentAlertBinding

class AlertFragment(
    private val label: Int = 0,
    private val isCancelable: Boolean = true,
    private val onPositiveClick: () -> Unit = {},
) : DialogFragment() {

    private var _binding: FragmentAlertBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAlertBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)

        dialog.setCancelable(isCancelable)
        dialog.setCanceledOnTouchOutside(isCancelable)

        if (!isCancelable) {
            dialog.setOnKeyListener { _, keyCode, _ ->
                keyCode == KeyEvent.KEYCODE_BACK // Close back press
            }
        }

        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            textLabel.text =
                if (label != 0) ContextCompat.getString(requireContext(), label) else ""

            if (!isCancelable) {
                textPositive.visibility = View.GONE
                textNegative.visibility = View.GONE
            }

            textPositive.setOnClickListener {
                onPositiveClick()
                dismiss()
            }

            textNegative.setOnClickListener { dismiss() }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}