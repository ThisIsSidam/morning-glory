package app.morning.glory.core.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

object AppPreferences {
    private lateinit var prefs: SharedPreferences

    // Keys
    const val ALARM_CODE_KEY = "alarm_code"

    // Initialize the SharedPreferences instance
    fun init(context: Context) {
        prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    }

    // Methods to register and unregister listeners
    fun registerListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        prefs.registerOnSharedPreferenceChangeListener(listener)
    }

    fun unregisterListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        prefs.unregisterOnSharedPreferenceChangeListener(listener)
    }

        /// The code which gets checked on alarms. The alarm does not dismiss without it.
    var alarmCode: String?
        get() = prefs.getString(ALARM_CODE_KEY, null)
        set(value) {
            prefs.edit { putString(ALARM_CODE_KEY, value) }
        }
}