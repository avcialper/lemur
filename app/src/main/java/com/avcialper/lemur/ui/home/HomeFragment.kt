package com.avcialper.lemur.ui.home

import android.Manifest.permission.POST_NOTIFICATIONS
import androidx.fragment.app.viewModels
import com.avcialper.lemur.R
import com.avcialper.lemur.data.AppManager
import com.avcialper.lemur.data.model.local.Task
import com.avcialper.lemur.databinding.FragmentHomeBinding
import com.avcialper.lemur.helper.PermissionManager
import com.avcialper.lemur.ui.BaseFragment
import com.avcialper.lemur.util.constant.FilterType
import com.avcialper.lemur.util.formatDate
import com.avcialper.owlcalendar.data.models.StartDate
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {

    private val vm: HomeViewModel by viewModels()

    override fun FragmentHomeBinding.initialize() {
        checkNotificationPermission()
        initUI()
        setListeners()
        observer()
    }

    private fun initUI() = with(binding) {
        vm.date = owlCalendar.startDate
        vm.todayDate = owlCalendar.startDate

        owlCalendar.changeLocale(Locale("tr", "TR"))
        scrollView.scrollY = vm.scrollPosition

        componentSelectedDate.apply {
            setTitle(vm.date!!)
        }
        componentToday.create(
            title = R.string.today,
            filterType = FilterType.TODAY,
            onSeeAllClickListener = ::navigateTasksPage
        )
        componentContinues.create(
            title = R.string.continues,
            filterType = FilterType.CONTINUES,
            onSeeAllClickListener = ::navigateTasksPage
        )
        componentCompleted.create(
            title = R.string.completed,
            filterType = FilterType.COMPLETED,
            onSeeAllClickListener = ::navigateTasksPage
        )
        componentCanceled.create(
            title = R.string.canceled,
            filterType = FilterType.CANCELED,
            onSeeAllClickListener = ::navigateTasksPage
        )
    }

    private fun setListeners() = with(binding) {
        fab.setOnClickListener {
            HomeFragmentDirections.toTaskCreate().navigate()
        }
        scrollView.setOnScrollChangeListener { _, _, scrollY, _, _ ->
            vm.scrollPosition = scrollY
        }
        owlCalendar.setOnDayClickListener { date ->
            val selectedDate = StartDate(date.year, date.month, date.dayOfMonth)
            if (vm.date != selectedDate) {
                vm.date = selectedDate
                componentSelectedDate.setTitle(date)
                componentSelectedDate.clearTasks()
                vm.getSelectedDateTasks()
            }
        }
        componentSelectedDate.setOnClickListener {
            val date = formatDate(vm.date!!)
            navigateTasksPage(FilterType.DATE, date)
        }

        componentSelectedDate.setOnTaskClickListener(::onTaskClick)
        componentToday.setOnTaskClickListener(::onTaskClick)
        componentContinues.setOnTaskClickListener(::onTaskClick)
        componentCompleted.setOnTaskClickListener(::onTaskClick)
        componentCanceled.setOnTaskClickListener(::onTaskClick)
    }

    private fun onTaskClick(task: Task) {
        val direction = HomeFragmentDirections.toTaskDetail(task.id)
        direction.navigate()
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

    private fun navigateTasksPage(type: FilterType = FilterType.ALL, filterDate: String? = null) {
        HomeFragmentDirections.toTasksFilter(filterDate, type).navigate()
    }

    private fun observer() = with(binding) {
        vm.selectedDateTasks.createResourceObserver(
            componentSelectedDate::handleSuccess,
            componentSelectedDate::handleLoading
        )
        vm.todayTasks.createResourceObserver(
            componentToday::handleSuccess,
            componentToday::handleLoading
        )
        vm.continuesTasks.createResourceObserver(
            componentContinues::handleSuccess,
            componentContinues::handleLoading
        )
        vm.completedTasks.createResourceObserver(
            componentCompleted::handleSuccess,
            componentCompleted::handleLoading
        )
        vm.canceledTasks.createResourceObserver(
            componentCanceled::handleSuccess,
            componentCanceled::handleLoading
        )
    }

    override fun onResume() {
        super.onResume()

        with(binding) {
            componentSelectedDate.clearTasks()
            componentToday.clearTasks()
            componentContinues.clearTasks()
            componentCompleted.clearTasks()
            componentCanceled.clearTasks()
        }

        val (year, month, dayOfMonth) = vm.date!!
        binding.owlCalendar.setStartDate(year, month, dayOfMonth)
        vm.getSelectedDateTasks()
        vm.getTodayTasks()
        vm.getContinuesTasks()
        vm.getCompletedTasks()
        vm.getCanceledTasks()
    }

}