package app.morning.glory.core.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

object AppPreferences {
    private lateinit var prefs: SharedPreferences

    // Keys
    const val SAVED_CODES_KEY = "saved_codes"

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

    // Methods to get and save codes
    fun getSavedCodes(): Set<String> {
        return prefs.getStringSet(SAVED_CODES_KEY, emptySet()) ?: emptySet()
    }

    fun saveCode(code: String) {
        val codes = getSavedCodes().toMutableSet()
        codes.add(code)
        prefs.edit { putStringSet(SAVED_CODES_KEY, codes) }
    }
}