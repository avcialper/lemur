package com.avcialper.lemur.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.avcialper.lemur.databinding.FragmentLoginBinding
import com.avcialper.lemur.helper.validator.EmailRule
import com.avcialper.lemur.helper.validator.EmptyRule
import com.avcialper.lemur.helper.validator.LengthRule
import com.avcialper.lemur.helper.validator.PasswordRule
import com.avcialper.lemur.helper.validator.validate

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonLogin.setOnClickListener {
            val isValid = validate()
            if (isValid)
                Toast.makeText(this@LoginFragment.context, "buyrun", Toast.LENGTH_SHORT).show()
        }
    }

    private fun validate(): Boolean {
        binding.apply {
            val isValidEmail = inputEmail.validate(
                rules = listOf(
                    EmptyRule(),
                    EmailRule()
                )
            )

            val isValidPassword = inputPassword.validate(
                rules = listOf(
                    EmptyRule(),
                    LengthRule(),
                    PasswordRule()
                )
            )

            return isValidEmail && isValidPassword
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}