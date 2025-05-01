package com.avcialper.lemur.ui.component

import android.content.DialogInterface
import android.icu.text.DateFormatSymbols
import android.icu.util.Calendar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import com.avcialper.lemur.R
import com.avcialper.lemur.databinding.FragmentDateTimePickerBinding
import com.avcialper.lemur.util.constant.DateTimePickerType
import com.avcialper.lemur.util.formatDate
import com.avcialper.lemur.util.formatTime
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.util.Locale

class DateTimePicker(
    private val type: DateTimePickerType,
    private val date: String? = null,
    private val hour: Int = 0,
    private val minute: Int = 0,
    private val title: String = "",
    private val onCompleted: (String) -> Unit,
    private val onDismiss: () -> Unit = {}
) : BottomSheetDialogFragment() {

    private var _binding: FragmentDateTimePickerBinding? = null
    private val binding get() = _binding!!

    private var firstPickerValue = 0
    private var secondPickerValue = 0
    private var thirdPickerValue = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDateTimePickerBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvTitle.apply {
            visibility = if (title.isEmpty())
                View.GONE
            else {
                text = title
                View.VISIBLE
            }
        }

        if (type == DateTimePickerType.DATE)
            handleDatePicker()
        else
            setupTimePicker()

        binding.buttonComplete.setOnClickListener {
            val data = if (type == DateTimePickerType.DATE)
                formatDate(firstPickerValue, secondPickerValue - 1, thirdPickerValue)
            else
                formatTime(firstPickerValue, thirdPickerValue)
            onCompleted(data)
            dismiss()
        }
    }

    private fun handleDatePicker() {
        val dateTitle = context?.getString(R.string.date)
        if (date == dateTitle) {
            val currentDate = Calendar.getInstance()
            val day = currentDate.get(Calendar.DAY_OF_MONTH)
            val month = currentDate.get(Calendar.MONTH) + 1
            val year = currentDate.get(Calendar.YEAR)
            setupDatePicker(day, month, year)
        } else {
            val splitDate = date?.split(".")
            if (splitDate != null)
                setupDatePicker(
                    splitDate[0].toInt(),
                    splitDate[1].toInt(),
                    splitDate[2].toInt()
                )
        }
    }

    private fun setupDatePicker(day: Int, month: Int, year: Int) = with(binding) {
        firstPickerValue = day
        secondPickerValue = month
        thirdPickerValue = year

        val totalDay = getTotalDay(month, year)
        firstPicker.create(1, totalDay, day, null) {
            firstPickerValue = it
        }

        secondPicker.create(1, 12, day, getLocalizedMonthNames()) {
            fixDay(it, thirdPickerValue)
            secondPickerValue = it
        }

        thirdPicker.create(year - 50, year + 50, year, null) {
            fixDay(secondPickerValue, it)
            thirdPickerValue = it
        }
    }

    private fun setupTimePicker() = with(binding) {
        secondPicker.visibility = View.GONE

        firstPicker.create(0, 23, hour, null) {
            firstPickerValue = it
        }

        thirdPicker.create(0, 59, minute, null) {
            thirdPickerValue = it
        }
    }

    private fun NumberPicker.create(
        min: Int,
        max: Int,
        value: Int,
        displayedValues: Array<String>?,
        onValueChange: (Int) -> Unit
    ) {
        this.minValue = min
        this.maxValue = max
        this.value = value
        this.displayedValues = displayedValues
        setOnValueChangedListener { _, _, newVal ->
            onValueChange(newVal)
        }
    }

    // Fix day picker when month or year is changed
    private fun fixDay(month: Int, year: Int) {
        val totalDay = getTotalDay(month, year)
        binding.firstPicker.apply {
            maxValue = totalDay
            if (firstPickerValue > totalDay) {
                value = totalDay
            }
        }
    }

    private fun getTotalDay(month: Int, year: Int): Int {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.MONTH, month - 1)
        calendar.set(Calendar.YEAR, year)
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    }

    private fun getLocalizedMonthNames(): Array<String> {
        val locale = Locale("tr", "TR")
        val symbols = DateFormatSymbols(locale)
        val monthNames = symbols.months.filter { it.isNotEmpty() }
        return monthNames.toTypedArray()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        onDismiss()
    }

}