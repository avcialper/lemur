package com.avcialper.lemur.ui.auth.signup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.avcialper.lemur.databinding.FragmentSignupBinding
import com.avcialper.lemur.helper.ImagePicker
import com.avcialper.lemur.helper.validator.ConfirmPasswordRule
import com.avcialper.lemur.helper.validator.EmailRule
import com.avcialper.lemur.helper.validator.EmptyRule
import com.avcialper.lemur.helper.validator.LengthRule
import com.avcialper.lemur.helper.validator.PasswordRule

class SignupFragment : Fragment() {

    private var _binding: FragmentSignupBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<SignupViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignupBinding.inflate(layoutInflater, container, false)
        val view = binding.root
        return view
    }

    private lateinit var imagePicker: ImagePicker
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imagePicker = ImagePicker(this) { uri ->
            binding.imageUser.setImageURI(uri)
        }

        binding.apply {
            imageUser.setOnClickListener {
                imagePicker.pickImage()
            }

            buttonSignup.setOnClickListener {
                val isValid = validate()

                if (isValid)
                    Toast.makeText(this@SignupFragment.context, "buyrun", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun validate(): Boolean {
        binding.apply {
            val isValidUsername = inputUsername.validate(
                rules = listOf(
                    EmptyRule(),
                    LengthRule(4, 16)
                )
            )

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

            val isValidConfirmPassword = inputConfirmPassword.validate(
                rules = listOf(
                    EmptyRule(),
                    LengthRule(),
                    PasswordRule(),
                    ConfirmPasswordRule(inputPassword.value)
                )
            )

            return isValidUsername && isValidEmail && isValidPassword && isValidConfirmPassword
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}