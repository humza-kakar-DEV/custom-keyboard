package com.mubashir.customkeyboard.ime

import android.content.Context

object KeyboardPreferences {
    private const val PREFS_NAME = "custom_keyboard_prefs"
    private const val KEY_SOURCE_LANGUAGE = "source_language_code"
    private const val KEY_TARGET_LANGUAGE = "target_language_code"

    fun getSourceLanguageCode(context: Context): String {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_SOURCE_LANGUAGE, "en") ?: "en"
    }

    fun getTargetLanguageCode(context: Context): String {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_TARGET_LANGUAGE, "ur") ?: "ur"
    }

    fun setSourceLanguageCode(context: Context, code: String) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_SOURCE_LANGUAGE, code)
            .apply()
    }

    fun setTargetLanguageCode(context: Context, code: String) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_TARGET_LANGUAGE, code)
            .apply()
    }
}
