package com.avcialper.lemur.ui.component

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.withStyledAttributes
import com.avcialper.lemur.R
import com.avcialper.lemur.databinding.ComponentTaskDetailFabBinding

class TaskDetailFAB @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding =
        ComponentTaskDetailFabBinding.inflate(LayoutInflater.from(context), this, true)

    private var isFABOpen = false
    private var fabCount = 2

    private var firstFABClickListener: () -> Unit = {}
    private var secondFABClickListener: () -> Unit = {}
    private var thirdFABClickListener: () -> Unit = {}
    private var fourthFABClickListener: () -> Unit = {}

    init {
        binding.apply {
            context.withStyledAttributes(attrs, R.styleable.CustomFAB, defStyleAttr, 0) {
                fabCount = getInteger(R.styleable.CustomFAB_fab_count, 2)
                val icon = getDrawable(R.styleable.CustomFAB_fab_icon)
                val firstFabText = getString(R.styleable.CustomFAB_first_fab_text)
                val secondFabText = getString(R.styleable.CustomFAB_second_fab_text)
                val thirdFabText = getString(R.styleable.CustomFAB_third_fab_text)
                val fourthFabText = getString(R.styleable.CustomFAB_fourth_fab_text)

                firstFab.text = firstFabText
                secondFab.text = secondFabText
                thirdFab.text = thirdFabText
                fourthFab.text = fourthFabText

                fifthFab.setImageDrawable(icon)
            }


            firstFab.hide()
            secondFab.hide()
            thirdFab.hide()
            fourthFab.hide()

            fifthFab.setOnClickListener {
                if (isFABOpen) {
                    firstFab.hide()
                    secondFab.hide()
                    if (fabCount != 2) {
                        thirdFab.hide()
                        fourthFab.hide()
                    }
                    isFABOpen = false
                } else {
                    firstFab.show()
                    secondFab.show()
                    if (fabCount != 2) {
                        thirdFab.show()
                        fourthFab.show()
                    }
                    isFABOpen = true
                }
            }

            firstFab.setOnClickListener {
                firstFABClickListener()
            }
            secondFab.setOnClickListener {
                secondFABClickListener()
            }
            thirdFab.setOnClickListener {
                thirdFABClickListener()
            }
            fourthFab.setOnClickListener {
                fourthFABClickListener()
            }
        }
    }

    fun handleLoading(isLoading: Boolean) = with(binding) {
        if (isLoading)
            fifthFab.hide()
        else
            fifthFab.show()
    }

    fun setFirstFabClickListener(listener: () -> Unit) {
        firstFABClickListener = listener
    }

    fun setSecondFabClickListener(listener: () -> Unit) {
        secondFABClickListener = listener
    }

    fun setThirdFabClickListener(listener: () -> Unit) {
        thirdFABClickListener = listener
    }

    fun setFourthFabClickListener(listener: () -> Unit) {
        fourthFABClickListener = listener
    }
}