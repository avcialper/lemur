package com.avcialper.lemur.ui.auth.forgot

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.avcialper.lemur.databinding.FragmentForgotPasswordBinding
import com.avcialper.lemur.helper.validator.EmailRule
import com.avcialper.lemur.helper.validator.EmptyRule

class ForgotPasswordFragment : Fragment() {

    private var _binding: FragmentForgotPasswordBinding? = null
    private val binding get() = _binding!!

    private val vm: ForgotPasswordViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentForgotPasswordBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonSend.setOnClickListener {
            val isValid = validate()
            if (isValid)
                Toast.makeText(requireContext(), "buyrun", Toast.LENGTH_SHORT).show()
        }
    }

    private fun validate(): Boolean {
        val isValidEmail = binding.inputEmail.validate(
            rules = listOf(
                EmptyRule(),
                EmailRule()
            )
        )
        return isValidEmail
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}