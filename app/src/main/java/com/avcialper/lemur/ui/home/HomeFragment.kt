package com.avcialper.lemur.ui.home

import android.Manifest.permission.POST_NOTIFICATIONS
import com.avcialper.lemur.R
import com.avcialper.lemur.data.AppManager
import com.avcialper.lemur.data.model.local.Task
import com.avcialper.lemur.databinding.FragmentHomeBinding
import com.avcialper.lemur.helper.PermissionManager
import com.avcialper.lemur.ui.BaseFragment
import com.avcialper.lemur.ui.component.TasksArea
import com.avcialper.lemur.util.constant.FilterType
import com.avcialper.lemur.util.constant.TaskStatus
import com.avcialper.lemur.util.constant.TaskType
import dagger.hilt.android.AndroidEntryPoint
import java.util.UUID

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {

    override fun FragmentHomeBinding.initialize() {
        checkNotificationPermission()
        initUI()
        setListeners()
    }

    private fun initUI() = with(binding) {
        val tasks = listOf(
            createTask(
                "cart curt görevi var, dobarlanın işimiz var ehehehe",
                "description1",
                "23.03.2025",
                type = TaskType.TEAM
            ),
            createTask(
                "task3",
                "description alsfkj sfkasf asfjasfn aksjf amsfn aksf aksfas faksf asfa sfaskjfasf aks fasfasnf aksfas faskn faskjf asf askfja sflaksf aksf askf aksnf aksf asnf aks fkas fk akaljslk",
                type = TaskType.MEET,
                status = TaskStatus.COMPLETED
            ),
            createTask(
                "keçisin keçi",
                "meeeee, iş başlattın mı iş pü",
                endTime = "15:00",
                status = TaskStatus.CANCELED
            ), createTask(
                "ehehehehehe",
                "meeeee, iş başlattın mı iş pü",
                endTime = "15:00",
                status = TaskStatus.CANCELED
            )
        )
        componentSelectedDate.apply {
            setTitle(owlCalendar.startDate)
            changeList(tasks)
            setOnSeeAllClickListener(::navigateTasksPage)
        }
        componentToday.create(title = R.string.today, filterType = FilterType.TODAY)
        componentContinues.create(title = R.string.continues, filterType = FilterType.CONTINUES)
        componentCompleted.create(title = R.string.completed, filterType = FilterType.COMPLETED)
        componentCanceled.create(title = R.string.canceled, filterType = FilterType.CANCELED)
    }

    private fun setListeners() = with(binding) {
        owlCalendar.setOnDayClickListener { date ->
            componentSelectedDate.setTitle(date)
        }
    }

    private fun checkNotificationPermission() {
        val permissionManager = PermissionManager(this@HomeFragment)
        if (permissionManager.isUpperTiramisu) {
            val isGranted = permissionManager.checkPermission(POST_NOTIFICATIONS)
            AppManager.isDeviceNotificationPermissionGranted = isGranted

            if (!isGranted)
                permissionManager.requestPermission(POST_NOTIFICATIONS) {
                    AppManager.isDeviceNotificationPermissionGranted = it
                }
        }
    }

    private fun navigateTasksPage(type: FilterType = FilterType.ALL) {
        HomeFragmentDirections.toTasks().apply {
            filterType = type
        }.navigate()
    }

    private fun TasksArea.create(
        tasks: List<Task> = emptyList(),
        title: Int,
        filterType: FilterType = FilterType.ALL
    ) {
        setTitle(title)
        changeList(tasks)
        setOnSeeAllClickListener { navigateTasksPage(filterType) }
    }

    private fun createTask(
        name: String,
        description: String,
        endDate: String? = null,
        endTime: String? = null,
        type: TaskType = TaskType.PERSONAL,
        status: TaskStatus = TaskStatus.CONTINUES
    ): Task {
        return Task(
            UUID.randomUUID().toString(),
            name,
            description,
            "22.03.2025",
            endDate,
            "12:00",
            endTime,
            type,
            status
        )
    }

}