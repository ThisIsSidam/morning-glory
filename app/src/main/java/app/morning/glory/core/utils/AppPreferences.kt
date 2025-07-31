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



    /**
     * The QR Code value used for locking dismiss
     */
    const val ALARM_CODE_KEY = "alarm_code"
    var alarmCode: String?
        get() = prefs.getString(ALARM_CODE_KEY, null)
        set(value) {
            prefs.edit { putString(ALARM_CODE_KEY, value) }
        }



    /**
     * The key for local time used to set daily alarms
     */
    const val DAILY_SLEEP_ALARM_KEY = "daily_sleep_alarm"

    /// Saving the local time of daily sleep alarm..
    /// Used for rescheduling the daily alarms
    var dailyAlarm: LocalTime?
        get() = prefs.getLocalTime(DAILY_SLEEP_ALARM_KEY, null)
        set(value) {
            prefs.edit { putLocalTime(DAILY_SLEEP_ALARM_KEY, value) }
        }


    /**
     * The key for saving sleep alarm time
     */
    const val ALARM_TIME = "sleep_alarm_time"

    /**
     * Saving the time of sleep alarms...
     * Saved when an alarm is scheduled
     * Used for rescheduling
     */
    var sleepAlarmTime: Calendar?
        get() = prefs.getTime(ALARM_TIME, null)
        set(value) {
            prefs.edit { putTime(ALARM_TIME, value) }
        }



    /**
     * The key for saving nap alarm time
     */
    const val NAP_TIME = "nap_alarm_time"

    /**
     * Saving the time of sleep alarms...
     * Saved when an alarm is scheduled
     * Used for rescheduling
     */
    var napAlarmTime: Calendar?
        get() = prefs.getTime(NAP_TIME, null)
        set(value) {
            prefs.edit { putTime(NAP_TIME, value) }
        }





    // The key, getter and setter for displaying time or duration of the upcoming alarm

    const val DISPLAY_SLEEP_DURATION_HINT_KEY = "sleep_time_hint"
    const val DISPLAY_NAP_DURATION_HINT_KEY = "nap_time_hint"

    var displaySleepDuration : Boolean
        get() = prefs.getBoolean(DISPLAY_SLEEP_DURATION_HINT_KEY, false)
        set(value) {
            prefs.edit { putBoolean(DISPLAY_SLEEP_DURATION_HINT_KEY, value) }
        }

    var displayNapDuration : Boolean
        get() = prefs.getBoolean(DISPLAY_NAP_DURATION_HINT_KEY, false)
        set(value) {
            prefs.edit { putBoolean(DISPLAY_NAP_DURATION_HINT_KEY, value) }
        }
}