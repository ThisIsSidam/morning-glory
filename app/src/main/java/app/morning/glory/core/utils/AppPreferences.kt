package app.morning.glory.core.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import app.morning.glory.core.extensions.getLocalTime
import app.morning.glory.core.extensions.getTime
import app.morning.glory.core.extensions.putLocalTime
import app.morning.glory.core.extensions.putTime
import java.time.LocalTime
import java.util.Calendar

object AppPreferences {
    private lateinit var prefs: SharedPreferences
    var isInitialized : Boolean = false

    // Keys
    const val ALARM_CODE_KEY = "alarm_code"
    const val DAILY_SLEEP_ALARM_KEY = "daily_sleep_alarm"
    const val ALARM_TIME = "sleep_alarm_time"

    // Initialize the SharedPreferences instance
    fun init(context: Context) {
        if (isInitialized) return
        prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        isInitialized = true
    }

    // Methods to register and unregister listeners
    fun registerListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        prefs.registerOnSharedPreferenceChangeListener(listener)
    }

    fun unregisterListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        prefs.unregisterOnSharedPreferenceChangeListener(listener)
    }

    /// Setters and Getters for various values used
    var alarmCode: String?
        get() = prefs.getString(ALARM_CODE_KEY, null)
        set(value) {
            prefs.edit { putString(ALARM_CODE_KEY, value) }
        }

    /// Saving the local time of daily sleep alarm..
    /// Used for rescheduling the daily alarms
    var dailyAlarm: LocalTime?
        get() = prefs.getLocalTime(DAILY_SLEEP_ALARM_KEY, null)
        set(value) {
            prefs.edit { putLocalTime(DAILY_SLEEP_ALARM_KEY, value) }
        }

    /// Saving the time of sleep alarms...
    /// Saved when an alarm is scheduled
    /// Used for rescheduling
    var sleepAlarmTime: Calendar?
        get() = prefs.getTime(ALARM_TIME, null)
        set(value) {
            prefs.edit { putTime(ALARM_TIME, value) }
        }
}