package app.morning.glory.core.utils

import android.content.Context

object CustomActions {
    fun alarmTriggered(context: Context) : String {
        return "${context.packageName}.ALARM_TRIGGERED"
    }
}