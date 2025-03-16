package com.avcialper.lemur.data

import com.avcialper.lemur.R
import com.avcialper.lemur.util.constant.Theme

object AppManager {

    var notificationPermission = false
    var theme = Theme.SYSTEM_DEFAULT

    fun notificationIconId(): Int {
        return if (notificationPermission)
            R.drawable.ic_notifications_active
        else
            R.drawable.ic_notifications_off
    }

    fun themeIconId(): Int {
        return when (theme) {
            Theme.SYSTEM_DEFAULT -> R.drawable.ic_system_default
            Theme.LIGHT -> R.drawable.ic_light_mode
            Theme.DARK -> R.drawable.ic_dark_mode
        }
    }

}