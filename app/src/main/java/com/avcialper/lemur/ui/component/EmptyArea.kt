package com.avcialper.lemur.ui.component

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.withStyledAttributes
import com.avcialper.lemur.R
import com.avcialper.lemur.databinding.ComponentEmptyAreaBinding

class EmptyArea @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding =
        ComponentEmptyAreaBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        initView(context, attrs)
    }

    private fun initView(context: Context, attrs: AttributeSet?) {
        attrs?.let {
            context.withStyledAttributes(it, R.styleable.EmptyArea) {
                val contentText = getString(R.styleable.EmptyArea_contentText)
                val buttonText = getString(R.styleable.EmptyArea_buttonText)

                binding.tvContent.text = contentText
                binding.buttonAction.text = buttonText
            }
        }

    }

    fun setButtonAction(onClick: () -> Unit) {
        binding.buttonAction.setOnClickListener { onClick() }
    }

    fun showActionButton(){
        binding.buttonAction.visibility = VISIBLE
    }

    fun hideActionButton(){
        binding.buttonAction.visibility = GONE
    }
}
