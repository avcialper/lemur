package com.avcialper.lemur.ui.auth.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.avcialper.lemur.databinding.FragmentLoginBinding
import com.avcialper.lemur.helper.validator.EmailRule
import com.avcialper.lemur.helper.validator.EmptyRule
import com.avcialper.lemur.helper.validator.LengthRule
import com.avcialper.lemur.helper.validator.PasswordRule
import com.avcialper.lemur.util.constants.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<LoginViewModel>()

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

        observeState()

        binding.apply {
            textSignup.setOnClickListener {
                val direction = LoginFragmentDirections.toSignup()
                direction.navigate()
            }

            textForgotPassword.setOnClickListener {
                val direction = LoginFragmentDirections.toForgot()
                direction.navigate()
            }

            buttonLogin.setOnClickListener {
                val isValid = validate()
                if (!isValid) return@setOnClickListener

                val email = inputEmail.value
                val password = inputPassword.value
                viewModel.login(email, password)
            }
        }
    }

    private fun observeState() {
        lifecycleScope.launch {
            viewModel.state.collect { state ->
                when (state) {
                    is Resource.Loading -> {
                        toast("Loading")
                    }

                    is Resource.Success -> {
                        toast("Success")
                    }

                    is Resource.Error -> {
                        toast("Error: ${state.throwable.toString()}")
                    }

                    else -> {}
                }
            }
        }
    }

    private fun toast(message: String) {
        val context = requireContext()
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun NavDirections.navigate() {
        val navController = findNavController()
        navController.navigate(this)
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