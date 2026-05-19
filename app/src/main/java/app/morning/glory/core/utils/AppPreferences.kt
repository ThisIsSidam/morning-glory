package app.morning.glory.core.utils

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import androidx.core.content.edit
import androidx.core.net.toUri
import app.morning.glory.core.audio.RingtoneInfo
import app.morning.glory.core.audio.UriTypeAdapter
import app.morning.glory.core.extensions.getDuration
import app.morning.glory.core.extensions.getLocalTime
import app.morning.glory.core.extensions.getTime
import app.morning.glory.core.extensions.putDuration
import app.morning.glory.core.extensions.putLocalTime
import app.morning.glory.core.extensions.putTime
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.time.LocalTime
import java.util.Calendar
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

object AppPreferences {
    var isInitialized: Boolean = false
    private lateinit var prefs: SharedPreferences
    private lateinit var gson: Gson

    // Initialize the SharedPreferences instance
    fun init(context: Context) {
        if (isInitialized) return
        prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        gson = GsonBuilder()
            .registerTypeAdapter(Uri::class.java, UriTypeAdapter())
            .create()
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
     * Saving the time of nap alarms...
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

    var displaySleepDuration: Boolean
        get() = prefs.getBoolean(DISPLAY_SLEEP_DURATION_HINT_KEY, false)
        set(value) {
            prefs.edit { putBoolean(DISPLAY_SLEEP_DURATION_HINT_KEY, value) }
        }

    var displayNapDuration: Boolean
        get() = prefs.getBoolean(DISPLAY_NAP_DURATION_HINT_KEY, false)
        set(value) {
            prefs.edit { putBoolean(DISPLAY_NAP_DURATION_HINT_KEY, value) }
        }

    // -- -- Nap Durations -- -- //

    const val NAP_DURATION_1 = "nap_duration_1"
    const val NAP_DURATION_2 = "nap_duration_2"
    const val NAP_DURATION_3 = "nap_duration_3"
    const val NAP_DURATION_4 = "nap_duration_4"

    var napDuration1: Duration
        get() = prefs.getDuration(NAP_DURATION_1, 15.minutes) ?: 15.minutes
        set(value) = prefs.edit { putDuration(NAP_DURATION_1, value) }

    var napDuration2: Duration
        get() = prefs.getDuration(NAP_DURATION_2, 20.minutes) ?: 20.minutes
        set(value) = prefs.edit { putDuration(NAP_DURATION_2, value) }

    var napDuration3: Duration
        get() = prefs.getDuration(NAP_DURATION_3, 30.minutes) ?: 30.minutes
        set(value) = prefs.edit { putDuration(NAP_DURATION_3, value) }

    var napDuration4: Duration
        get() = prefs.getDuration(NAP_DURATION_4, 45.minutes) ?: 45.minutes
        set(value) = prefs.edit { putDuration(NAP_DURATION_4, value) }


    // -- -- Ringtone preferences -- -- //

    // List of saved ringtones

    const val RINGTONE_LIST_KEY = "ringtone_list"

    fun getRingtoneList(): List<RingtoneInfo> {
        val json = prefs.getString(RINGTONE_LIST_KEY, null) ?: return emptyList()
        return try {
            val type = com.google.gson.reflect.TypeToken.getParameterized(
                List::class.java,
                RingtoneInfo::class.java
            ).type
            gson.fromJson<List<RingtoneInfo>>(json, type) ?: emptyList()
        } catch (_: Exception) {
            emptyList()
        }
    }

    fun addRingtone(ringtone: RingtoneInfo) {
        val list = getRingtoneList().toMutableList()
        if (list.none { it.uri == ringtone.uri }) {
            list.add(ringtone)
            saveRingtoneList(list)
        }
    }

    fun removeRingtone(ringtone: RingtoneInfo) {
        val list = getRingtoneList().toMutableList()
        val newList = list.filter { it.uri != ringtone.uri }
        saveRingtoneList(newList)
    }

    private fun saveRingtoneList(list: List<RingtoneInfo>) {
        val json = gson.toJson(list)
        prefs.edit { putString(RINGTONE_LIST_KEY, json) }
    }

    // Selected ringtone URI
    const val SELECTED_RINGTONE_KEY = "selected_ringtone"
    var selectedRingtone: Uri?
        get() = prefs.getString(SELECTED_RINGTONE_KEY, null)?.toUri()
        set(value) {
            prefs.edit { putString(SELECTED_RINGTONE_KEY, value?.toString()) }
        }

    // Is ringtone randomization enabled
    const val RANDOMIZE_RINGTONES_KEY = "randomize_ringtones"
    var randomizeRingtones: Boolean
        get() = prefs.getBoolean(RANDOMIZE_RINGTONES_KEY, false)
        set(value) {
            prefs.edit { putBoolean(RANDOMIZE_RINGTONES_KEY, value) }
        }


    // User customizable options


    const val MAX_SNOOZE_COUNT_KEY = "max_snooze_count"
    const val DEFAULT_MAX_SNOOZE_COUNT = 2
    var maxSnoozeCount: Int
        get() = prefs.getInt(MAX_SNOOZE_COUNT_KEY, DEFAULT_MAX_SNOOZE_COUNT)
        set(value) {
            prefs.edit { putInt(MAX_SNOOZE_COUNT_KEY, value) }
        }


    const val SNOOZE_DURATION_KEY = "snooze_duration"
    const val DEFAULT_SNOOZE_DURATION = 10 // in minutes

    /**
     * The duration for which the alarm will be snoozed
     * Int is returned.. that is the number of minutes
     * Default is 10 minutes
     */
    var snoozeDurationMinutes: Int
        get() = prefs.getInt(SNOOZE_DURATION_KEY, DEFAULT_SNOOZE_DURATION)
        set(value) {
            prefs.edit { putInt(SNOOZE_DURATION_KEY, value) }
        }


    const val WAKE_CHECK_ALARM_TIME_KEY = "pre_alarm_notification_time"
    const val DEFAULT_PRE_ALARM_NOTIFICATION_TIME = 30 // in minutes

    /**
     * The time before the alarm when the pre-alarm notification will be shown
     * Default is 30 minutes earlier
     */
    var wakeCheckAlarmTime: Int
        get() = prefs.getInt(WAKE_CHECK_ALARM_TIME_KEY, DEFAULT_PRE_ALARM_NOTIFICATION_TIME)
        set(value) {
            prefs.edit { putInt(WAKE_CHECK_ALARM_TIME_KEY, value) }
        }
}
