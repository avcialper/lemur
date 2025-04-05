package com.avcialper.lemur.ui.component

import android.icu.text.DateFormatSymbols
import android.icu.util.Calendar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import com.avcialper.lemur.R
import com.avcialper.lemur.databinding.FragmentDateSelectorBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.util.Locale

class DateSelector(
    private val date: String?,
    private val onDateSelected: (String) -> Unit
) : BottomSheetDialogFragment() {

    private var _binding: FragmentDateSelectorBinding? = null
    private val binding get() = _binding!!

    private var selectedDay = 0
    private var selectedMonth = 0
    private var selectedYear = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDateSelectorBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dateTitle = context?.getString(R.string.date)
        if (date == dateTitle) {
            val currentDate = Calendar.getInstance()
            val day = currentDate.get(Calendar.DAY_OF_MONTH)
            val month = currentDate.get(Calendar.MONTH) + 1
            val year = currentDate.get(Calendar.YEAR)
            setupNumberPickers(day, month, year)
        } else {
            val splitDate = date?.split(".")
            if (splitDate != null)
                setupNumberPickers(
                    splitDate[0].toInt(),
                    splitDate[1].toInt(),
                    splitDate[2].toInt()
                )
        }

        binding.buttonComplete.setOnClickListener {
            val date = String.format(
                Locale.getDefault(),
                "%02d.%02d.%04d",
                selectedDay,
                selectedMonth,
                selectedYear
            )
            onDateSelected(date)
            dismiss()
        }
    }

    private fun setupNumberPickers(day: Int, month: Int, year: Int) = with(binding) {
        selectedDay = day
        selectedMonth = month
        selectedYear = year

        val totalDay = getTotalDay(month, year)
        npDay.create(1, totalDay, day, null) {
            selectedDay = it
        }

        npMonth.create(1, 12, day, getLocalizedMonthNames()) {
            fixDay(it, selectedYear)
            selectedMonth = it
        }

        npYear.create(year - 50, year + 50, year, null) {
            fixDay(selectedMonth, it)
            selectedYear = it
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
        binding.npDay.apply {
            maxValue = totalDay
            if (selectedDay > totalDay) {
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

}