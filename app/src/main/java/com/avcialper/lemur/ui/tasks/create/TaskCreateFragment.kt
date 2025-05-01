package com.avcialper.lemur.ui.tasks.create

import android.net.Uri
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.avcialper.lemur.R
import com.avcialper.lemur.databinding.FragmentTaskCreateBinding
import com.avcialper.lemur.helper.ImagePicker
import com.avcialper.lemur.helper.UriToFile
import com.avcialper.lemur.helper.validator.EmptyRule
import com.avcialper.lemur.helper.validator.LengthRule
import com.avcialper.lemur.ui.BaseFragment
import com.avcialper.lemur.ui.component.DateTimePicker
import com.avcialper.lemur.ui.component.ImageUpdateSheet
import com.avcialper.lemur.ui.component.TaskTypeSheet
import com.avcialper.lemur.util.constant.DateTimePickerType
import com.avcialper.lemur.util.constant.Resource
import com.avcialper.lemur.util.constant.TaskType
import com.avcialper.lemur.util.extension.exceptionConverter
import com.avcialper.lemur.util.formatDate
import com.avcialper.lemur.util.formatTime
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.util.UUID

@AndroidEntryPoint
class TaskCreateFragment :
    BaseFragment<FragmentTaskCreateBinding>(FragmentTaskCreateBinding::inflate) {

    private val vm: TaskCreateViewModel by viewModels()

    private var rangeDate = ""
    private var startTime = ""
    private var endTime = ""
    private var isStartTimeSelectionOpened = false
    private var isEndTimeSelectionOpened = false
    private var imageUri: Uri? = null
    private var type: TaskType? = null
    private lateinit var imagePicker: ImagePicker

    override fun FragmentTaskCreateBinding.initialize() {
        imagePicker = ImagePicker(this@TaskCreateFragment) { uri ->
            imageUri = uri
            binding.apply {
                taskImage.setImageURI(uri)
                taskImage.visibility = View.VISIBLE
                addImage.visibility = View.GONE
            }
        }
        initUI()
        observer()
    }

    private fun initUI() = with(binding) {

        tvSelectedDate.text = formatDate(owlCalendar.startDate)

        owlCalendar.apply {
            setOnDayClickListener {
                if (rangeDate.isEmpty())
                    tvSelectedDate.text = it.date
            }

            setOnLineDateChangeListener { startDate, endDate ->
                if (startDate == null || endDate == null) {
                    rangeDate = ""
                    return@setOnLineDateChangeListener
                }
                rangeDate = "${formatDate(startDate)} - ${formatDate(endDate)}"
                tvSelectedDate.text = rangeDate
            }
        }

        tvSelectedTime.setOnClickListener {
            isStartTimeSelectionOpened = true
            openPicker(R.string.select_start_time)
        }

        tvType.setOnClickListener {
            TaskTypeSheet {
                type = it
                tvType.text = getString(it.messageId)
            }.show(this@TaskCreateFragment.childFragmentManager, "selector")
        }

        addImage.setOnClickListener {
            imagePicker.pickImage()
        }

        taskImage.setOnClickListener {
            ImageUpdateSheet(::deleteImage, imagePicker::pickImage)
                .show(parentFragmentManager, "selector")
        }

        buttonCreate.setOnClickListener {
            val isValid = validate()
            if (isValid) {
                var imageFile: File? = null
                if (imageUri != null)
                    imageFile = convertToFile(imageUri!!)
                binding.apply {
                    vm.createTask(
                        tvSelectedDate.text.toString().trim(),
                        tvSelectedTime.text.toString().trim(),
                        imageFile,
                        inputSubject.value,
                        inputContent.value,
                        type!!
                    )
                }
            }
        }

    }

    private fun onTimeSelected(data: String) {
        if (startTime.isEmpty())
            startTime = data
        else
            endTime = data
    }

    private fun onDismiss() {
        // check if the start and end time selection bottom sheet is opened
        if (isStartTimeSelectionOpened && isEndTimeSelectionOpened) {
            if (startTime.isNotEmpty() && endTime.isNotEmpty())
                binding.tvSelectedTime.text = formatTime(startTime, endTime)

            startTime = ""
            endTime = ""
            isStartTimeSelectionOpened = false
            isEndTimeSelectionOpened = false
        }

        // open the end time selection bottom sheet when start time is selected
        if (startTime.isNotEmpty()) {
            isEndTimeSelectionOpened = true
            openPicker(R.string.select_end_time)
        }
    }

    private fun openPicker(titleId: Int) {
        val title = context?.getString(titleId) ?: ""
        DateTimePicker(
            DateTimePickerType.TIME,
            title = title,
            onCompleted = ::onTimeSelected,
            onDismiss = ::onDismiss
        ).show(childFragmentManager, "time_picker")
    }

    private fun deleteImage() = with(binding) {
        imageUri = null
        addImage.visibility = View.VISIBLE
        taskImage.visibility = View.GONE
    }

    private fun observer() {
        vm.state.createObserver(::handleResource)
    }

    private fun handleResource(resource: Resource<Boolean>?) {
        when (resource) {
            is Resource.Error -> {
                loadingState(false)
                val errorMessage = requireContext().exceptionConverter(resource.throwable!!)
                toast(errorMessage)
            }

            is Resource.Loading -> loadingState(true)
            is Resource.Success -> handleSuccess()
            null -> loadingState(false)
        }
    }

    private fun loadingState(isLoading: Boolean) = with(binding) {
        owlCalendar.updateLoadingState(isLoading)
        tvSelectedDate.updateLoadingState(isLoading)
        tvSelectedTime.updateLoadingState(isLoading)
        tvType.updateLoadingState(isLoading)
        addImage.updateLoadingState(isLoading)
        taskImage.updateLoadingState(isLoading)
        inputSubject.setLoadingState(isLoading)
        inputContent.setLoadingState(isLoading)
        buttonCreate.updateLoadingState(isLoading)
    }

    private fun handleSuccess() {
        loadingState(false)
        findNavController().popBackStack()
    }

    private fun validate(): Boolean = with(binding) {
        val isTimeEmpty = tvSelectedTime.text == getString(R.string.select_time)
        if (isTimeEmpty)
            toast(R.string.select_time)

        val isTypeEmpty = tvType.text == getString(R.string.select_task_type)
        if (isTypeEmpty)
            toast(R.string.select_task_type)

        val isValidSubject = inputSubject.validate(
            rules = listOf(
                EmptyRule(),
                LengthRule(1, 50, R.string.subject_length_error)
            )
        )

        val isValidContent = inputContent.validate(
            rules = listOf(
                EmptyRule(),
                LengthRule(1, 250, R.string.subject_length_error)
            )
        )

        return isTimeEmpty.not() && isTypeEmpty.not() && isValidSubject && isValidContent
    }

    private fun convertToFile(uri: Uri): File =
        UriToFile(requireContext()).convert(UUID.randomUUID().toString(), uri)

}