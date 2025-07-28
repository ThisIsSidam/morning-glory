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
    const val ONCE_OFF_SLEEP_ALARM_KEY = "once_off_sleep_alarm"

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

    /// Saving the time of daily sleep alarm..
    /// Used for rescheduling after alarm and after reboot
    var dailyAlarm: LocalTime?
        get() = prefs.getLocalTime(DAILY_SLEEP_ALARM_KEY, null)
        set(value) {
            prefs.edit { putLocalTime(DAILY_SLEEP_ALARM_KEY, value) }
        }

    /// Saving the time of once off sleep alarm...
    /// Used mainly for rescheduling after reboot
    var onceOffAlarm: Calendar?
        get() = prefs.getTime(ONCE_OFF_SLEEP_ALARM_KEY, null)
        set(value) {
            prefs.edit { putTime(ONCE_OFF_SLEEP_ALARM_KEY, value) }
        }
}