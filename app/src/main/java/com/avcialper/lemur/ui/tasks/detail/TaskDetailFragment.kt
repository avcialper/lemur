package com.avcialper.lemur.ui.tasks.detail

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.TypedValue
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.activity.addCallback
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.avcialper.lemur.R
import com.avcialper.lemur.data.model.local.Task
import com.avcialper.lemur.databinding.FragmentTaskDetailBinding
import com.avcialper.lemur.helper.Divider
import com.avcialper.lemur.ui.BaseFragment
import com.avcialper.lemur.ui.MainActivity
import com.avcialper.lemur.ui.component.AlertFragment
import com.avcialper.lemur.ui.tasks.detail.note.NoteAdapter
import com.avcialper.lemur.util.concatStartAndEndDate
import com.avcialper.lemur.util.concatStartAndEntTime
import com.avcialper.lemur.util.constant.TaskStatus
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TaskDetailFragment :
    BaseFragment<FragmentTaskDetailBinding>(FragmentTaskDetailBinding::inflate) {

    private val vm: TaskDetailViewModel by viewModels()

    private val args: TaskDetailFragmentArgs by navArgs()

    override fun FragmentTaskDetailBinding.initialize() {
        @Suppress("DEPRECATION")
        (activity as MainActivity).window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        vm.getTaskDetail(args.taskId)
        initUI()
        observe()
        setListeners()
        handleBackPress()
    }

    private fun initUI() = with(binding) {
        val noteAdapter = NoteAdapter(emptyList())
        val noteLayoutManager = LinearLayoutManager(context)
        val divider = Divider(requireContext())
        rvNotes.apply {
            adapter = noteAdapter
            layoutManager = noteLayoutManager
            addItemDecoration(divider)
        }
    }

    private fun observe() {
        vm.state.createResourceObserver(::handleSuccess, ::handleLoading)
        vm.noteState.createResourceObserver(::handleNoteSuccess, ::handleLoading)
        vm.statusState.createResourceObserver(::handleStatusSuccess, ::handleLoading)
    }

    private fun handleSuccess(task: Task?) = with(binding) {
        task?.let {
            val (_, _, name, description, startDate, endDate, startTime, endTime, imageUrl, type, status, notes) = task

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

            (rvNotes.adapter as NoteAdapter).setData(notes)

        }
    }

    private fun handleNoteSuccess(isSuccess: Boolean?) = with(binding) {
        if (isSuccess == true) {
            closeCommentArea()
            vm.clearNoteState()
            vm.getTaskDetail(args.taskId)
            etNote.text.clear()
        }
    }

    private fun handleLoading(isLoading: Boolean) = with(binding) {
        progress.visibility = if (isLoading) View.VISIBLE else View.GONE
        wrapper.visibility = if (isLoading) View.GONE else View.VISIBLE
        if (isLoading) fab.hide() else fab.show()

        if (isLoading && noteWrapper.isVisible)
            noteWrapper.visibility = View.INVISIBLE
        else if (!isLoading && noteWrapper.isInvisible)
            noteWrapper.visibility = View.VISIBLE
    }

    private fun setListeners() = with(binding) {
        fab.apply {
            setFirstFabClickListener {
                val direction =
                    TaskDetailFragmentDirections.toTaskUpdate(vm.state.value?.data ?: Task())
                direction.navigate()
            }
            setSecondFabClickListener {
                noteWrapper.visibility = View.VISIBLE
                etNote.requestFocus()
                val imm =
                    context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(etNote, InputMethodManager.SHOW_IMPLICIT)
                fab.hide()
                handleNavigationBarColor(true)
            }
            setThirdFabClickListener {
                openAlertDialog(R.string.cancel_task_message) {
                    vm.updateTaskStatus(args.taskId, TaskStatus.CANCELED)
                }
            }
            setFourthFabClickListener {
                openAlertDialog(R.string.complete_task_message) {
                    vm.updateTaskStatus(args.taskId, TaskStatus.COMPLETED)
                }
            }
        }

        btnAddNote.setOnClickListener {
            val note = etNote.text.toString().trim()
            if (note.isNotEmpty())
                vm.addNote(vm.state.value!!.data!!.id, note)
        }

        nestedScroll.setOnScrollChangeListener { _, _, scrollY, _, oldScrollY ->
            if (scrollY > (oldScrollY + 10) && fab.isVisible())
                fab.hide()
            else if (scrollY < (oldScrollY - 10) && fab.isGone())
                fab.show()
        }
    }

    private fun getDrawable(id: Int): Drawable? =
        ContextCompat.getDrawable(requireContext(), id)?.mutate()

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as MainActivity).window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
    }

    private fun handleBackPress() = with(binding) {
        val mainActivity = activity as MainActivity
        mainActivity.onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (noteWrapper.isVisible) {
                closeCommentArea()
                isEnabled = false
            } else
                goBack()
        }
    }

    private fun handleNavigationBarColor(isNoteWrapperOpen: Boolean) {
        val typedValue = TypedValue()
        val theme = requireContext().theme
        val colorAttr = if (isNoteWrapperOpen) R.attr.bottomMenuColor else R.attr.backgroundColor
        theme.resolveAttribute(colorAttr, typedValue, true)
        @Suppress("DEPRECATION")
        (activity as MainActivity).window.navigationBarColor = typedValue.data
    }

    private fun closeCommentArea() = with(binding) {
        noteWrapper.visibility = View.GONE
        fab.show()
        handleNavigationBarColor(false)
        val imm =
            context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(etNote.windowToken, 0)
    }

    private fun openAlertDialog(label: Int, onCompleted: () -> Unit) {
        binding.fab.close()
        AlertFragment(label, onPositiveClick = onCompleted).show(childFragmentManager, "alert")
    }

    private fun handleStatusSuccess() {
        vm.clearStatusState()
        vm.getTaskDetail(args.taskId)
    }

}