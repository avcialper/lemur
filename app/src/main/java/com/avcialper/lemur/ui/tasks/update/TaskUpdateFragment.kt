package com.avcialper.lemur.ui.tasks.update

import android.net.Uri
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import coil.load
import com.avcialper.lemur.R
import com.avcialper.lemur.data.model.local.Task
import com.avcialper.lemur.databinding.FragmentTaskUpdateBinding
import com.avcialper.lemur.helper.ImagePicker
import com.avcialper.lemur.helper.UriToFile
import com.avcialper.lemur.helper.validator.EmptyRule
import com.avcialper.lemur.helper.validator.MaxLengthRule
import com.avcialper.lemur.ui.BaseFragment
import com.avcialper.lemur.ui.component.AlertFragment
import com.avcialper.lemur.ui.component.DateTimePicker
import com.avcialper.lemur.ui.component.ImageUpdateSheet
import com.avcialper.lemur.ui.component.TaskTypeSheet
import com.avcialper.lemur.util.concatStartAndEndDate
import com.avcialper.lemur.util.concatStartAndEntTime
import com.avcialper.lemur.util.constant.DateTimePickerType
import com.avcialper.lemur.util.extension.formatInvalidLengthError
import com.avcialper.lemur.util.formatDate
import com.avcialper.lemur.util.formatTime
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.util.UUID

@AndroidEntryPoint
class TaskUpdateFragment :
    BaseFragment<FragmentTaskUpdateBinding>(FragmentTaskUpdateBinding::inflate) {

    private val vm: TaskUpdateViewModel by viewModels()
    private val args: TaskUpdateFragmentArgs by navArgs()
    private lateinit var task: Task
    private lateinit var imagePicker: ImagePicker

    private var rangeDate = ""
    private var imageUri: Uri? = null
    private var startTime = ""
    private var endTime = ""
    private var isStartTimeSelectionOpened = false
    private var isEndTimeSelectionOpened = false

    override fun FragmentTaskUpdateBinding.initialize() {
        task = args.task
        imagePicker = ImagePicker(this@TaskUpdateFragment) {
            imageUri = it
            binding.apply {
                taskImage.load(it)
                taskImage.visibility = View.VISIBLE
                addImage.visibility = View.GONE
            }
        }
        initUI()
        setListeners()
        observe()
    }

    private fun initUI() = with(binding) {
        // TODO owlcalendar'ı set et
        val (_, _, name, description, startDate, endDate, startTime, endTime, imageUrl, type, _, _) = task

        inputSubject.value = name
        inputDescription.value = description
        tvSelectedDate.text =
            if (endDate == null) startDate else concatStartAndEndDate(startDate, endDate)
        tvSelectedTime.text = concatStartAndEntTime(startTime, endTime)
        imageUrl?.let {
            addImage.visibility = View.GONE
            taskImage.apply {
                load(it)
                visibility = View.VISIBLE
            }
        }
        tvType.text = type.name
    }

    private fun setListeners() = with(binding) {
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

        addImage.setOnClickListener {
            imagePicker.pickImage()
        }

        taskImage.setOnClickListener {
            ImageUpdateSheet(::deleteImage, imagePicker::pickImage)
                .show(parentFragmentManager, "selector")
        }

        tvType.setOnClickListener {
            TaskTypeSheet {
                task.type = it
                tvType.text = getString(it.messageId)
            }.show(parentFragmentManager, "selector")
        }

        tvSelectedTime.setOnClickListener {
            isStartTimeSelectionOpened = true
            openPicker(R.string.select_start_time)
        }

        buttonUpdate.setOnClickListener {
            val isValid = validate()
            if (isValid) {
                var imageFile: File? = null
                if (imageUri != null)
                    imageFile = convertToFile(imageUri!!)

                val selectedDate = binding.tvSelectedDate.text.trim()
                val splitDate = selectedDate.split("-")
                val startDate = splitDate[0].trim()
                val endDate = if (selectedDate.contains("-")) splitDate[1].trim() else null

                val selectedTime = binding.tvSelectedTime.text.trim()
                val splitTime = selectedTime.split("-")
                startTime = splitTime[0].trim()
                endTime = splitTime[1].trim()

                task.name = inputSubject.value
                task.description = inputDescription.value
                task.startDate = startDate
                task.endDate = endDate
                task.startTime = startTime
                task.endTime = endTime

                vm.updateTask(task, imageFile)
            }
        }

        buttonDelete.setOnClickListener {
            AlertFragment(R.string.delete_task_message, true) {
                vm.deleteTask(task.id)
            }.show(parentFragmentManager, "alert")
        }

    }

    private fun deleteImage() = with(binding) {
        imageUri = null
        task.imageUrl = null
        addImage.visibility = View.VISIBLE
        taskImage.visibility = View.GONE
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

    private fun onTimeSelected(data: String) {
        if (startTime.isEmpty())
            startTime = data
        else
            endTime = data
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
                MaxLengthRule(50)
            ),
            formatErrorMessage = { errorMessage ->
                errorMessage.formatInvalidLengthError(requireContext(), R.string.subject, 50)
            }
        )

        val isValidContent = inputDescription.validate(
            rules = listOf(
                EmptyRule(),
                MaxLengthRule(500)
            ),
            formatErrorMessage = { errorMessage ->
                errorMessage.formatInvalidLengthError(requireContext(), R.string.description, 500)
            }
        )

        return isTimeEmpty.not() && isTypeEmpty.not() && isValidSubject && isValidContent
    }

    private fun convertToFile(uri: Uri): File =
        UriToFile(requireContext()).convert(UUID.randomUUID().toString(), uri)

    private fun observe() {
        vm.state.createResourceObserver(::handleSuccess, ::handleLoading)
        vm.deleteState.createResourceObserver(::handleDeleteSuccess, ::handleLoading)
    }

    private fun handleSuccess() {
        goBack()
    }

    private fun handleDeleteSuccess() {
        goBack(R.id.homeFragment)
    }

    private fun handleLoading(isLoading: Boolean) = with(binding) {
        owlCalendar.updateLoadingState(isLoading)
        tvSelectedDate.updateLoadingState(isLoading)
        tvSelectedTime.updateLoadingState(isLoading)
        tvType.updateLoadingState(isLoading)
        addImage.updateLoadingState(isLoading)
        taskImage.updateLoadingState(isLoading)
        inputSubject.setLoadingState(isLoading)
        inputDescription.setLoadingState(isLoading)
        buttonUpdate.updateLoadingState(isLoading)
        buttonDelete.updateLoadingState(isLoading)
    }
}