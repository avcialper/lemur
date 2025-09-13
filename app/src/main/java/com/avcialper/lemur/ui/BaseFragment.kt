package com.avcialper.lemur.ui

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.avcialper.lemur.helper.UriToFile
import com.avcialper.lemur.util.constant.Resource
import com.avcialper.lemur.util.extension.exceptionConverter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.io.File
import java.util.UUID

abstract class BaseFragment<VB : ViewBinding>(
    private val inflate: (LayoutInflater, ViewGroup?, Boolean) -> VB,
) : Fragment() {

    private var _binding: VB? = null
    protected val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = inflate.invoke(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.initialize()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    abstract fun VB.initialize()

    fun View.updateLoadingState(isLoading: Boolean) {
        alpha = if (isLoading) 0.5f else 1f
        isEnabled = !isLoading
    }

    fun NavDirections.navigate() {
        val navController = findNavController()
        navController.navigate(this)
    }

    fun goBack() {
        val navController = findNavController()
        navController.popBackStack()
    }

    fun goBack(fragmentId: Int) {
        val navController = findNavController()
        navController.popBackStack(fragmentId, false)
    }

    fun <T> Flow<T>.createObserver(action: (T) -> Unit) {
        onEach(action).launchIn(viewLifecycleOwner.lifecycleScope)
    }

    fun <T> StateFlow<Resource<T>?>.createResourceObserver(
        handleSuccess: () -> Unit,
        handleLoading: (Boolean) -> Unit,
        handleError: ((String) -> Unit)? = null,
        handleException: ((Exception) -> Unit)? = null
    ) {
        onEach { resource ->
            when (resource) {
                is Resource.Loading -> handleLoading(true)
                is Resource.Success -> {
                    handleLoading(false)
                    handleSuccess()
                }

                is Resource.Error -> {
                    handleLoading(false)

                    if (handleException != null) {
                        handleException(resource.throwable!!)
                        return@onEach
                    }

                    val errorMessage = requireContext().exceptionConverter(resource.throwable!!)
                    if (handleError != null)
                        handleError(errorMessage)
                    else
                        toast(errorMessage)
                }

                null -> Unit
            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    fun <T> StateFlow<Resource<T>?>.createResourceObserver(
        handleSuccess: (T?) -> Unit,
        handleLoading: (Boolean) -> Unit,
        handleError: ((String) -> Unit)? = null,
        handleException: ((Exception) -> Unit)? = null
    ) {
        onEach { resource ->
            when (resource) {
                is Resource.Loading -> handleLoading(true)
                is Resource.Success -> {
                    handleLoading(false)
                    handleSuccess(resource.data)
                }

                is Resource.Error -> {
                    handleLoading(false)

                    if (handleException != null) {
                        handleException(resource.throwable!!)
                        return@onEach
                    }

                    val errorMessage = requireContext().exceptionConverter(resource.throwable!!)
                    if (handleError != null)
                        handleError(errorMessage)
                    else
                        toast(errorMessage)
                }

                null -> Unit
            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    fun <T> StateFlow<Resource<T>?>.createResourceObserverWithoutLoadingState(handleSuccess: (T?) -> Unit) {
        onEach { resource ->
            when (resource) {
                is Resource.Loading -> Unit
                is Resource.Success -> handleSuccess(resource.data)
                is Resource.Error -> toast(resource.throwable!!.message.toString())
                null -> Unit
            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    fun Uri.convertFile(): File =
        UriToFile(requireContext()).convert(UUID.randomUUID().toString(), this)

    fun toast(message: String) {
        val context = requireContext()
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun toast(messageId: Int) {
        val message = getString(messageId)
        toast(message)
    }

    fun getInt(id: Int): Int = requireContext().resources.getInteger(id)
}