package com.avcialper.lemur.ui.tasks.detail

import android.graphics.drawable.Drawable
import android.view.Gravity
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import coil.load
import com.avcialper.lemur.R
import com.avcialper.lemur.data.model.local.Task
import com.avcialper.lemur.databinding.FragmentTaskDetailBinding
import com.avcialper.lemur.ui.BaseFragment
import com.avcialper.lemur.util.concatStartAndEndDate
import com.avcialper.lemur.util.concatStartAndEntTime
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TaskDetailFragment :
    BaseFragment<FragmentTaskDetailBinding>(FragmentTaskDetailBinding::inflate) {

    private val vm: TaskDetailViewModel by viewModels()

    private val args: TaskDetailFragmentArgs by navArgs()

    override fun FragmentTaskDetailBinding.initialize() {
        vm.getTaskDetail(args.taskId)
        observe()
    }

    private fun observe() {
        vm.state.createResourceObserver(::handleSuccess, ::handleLoading)
    }

    private fun handleSuccess(task: Task?) = with(binding) {
        task?.let {
            val (_, _, name, description, startDate, endDate, startTime, endTime, imageUrl, type, status) = task

            tvTitle.text = name
            tvDate.text =
                if (endDate != null) concatStartAndEndDate(startDate, endDate) else startDate
            tvTime.text = concatStartAndEntTime(startTime, endTime)
            tvDescription.text = description
            imageUrl?.let {
                imageTask.apply {
                    load(it)
                    visibility = View.VISIBLE
                }
            }

            val statusDrawable = getDrawable(R.drawable.oval_background)
            statusDrawable?.let {
                val color = ContextCompat.getColor(root.context, status.colorId)
                DrawableCompat.setTint(it, color)
            }

            val typeDrawable = getDrawable(type.drawableId)

            tvTitle.setCompoundDrawablesWithIntrinsicBounds(
                typeDrawable,
                null,
                statusDrawable,
                null
            )

        }
    }

    private fun handleLoading(isLoading: Boolean) = with(binding) {
        progress.visibility = if (isLoading) View.VISIBLE else View.GONE
        root.gravity = if (isLoading) Gravity.CENTER else Gravity.START

        val visibility = if (isLoading) View.GONE else View.VISIBLE
        tvTitle.visibility = visibility
        tvDate.visibility = visibility
        tvTime.visibility = visibility
        tvDescription.visibility = visibility
    }

    private fun getDrawable(id: Int): Drawable? =
        ContextCompat.getDrawable(requireContext(), id)?.mutate()

}