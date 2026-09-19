package com.example.data

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("teacher_session_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_TEACHER_ID = "teacher_id"
        private const val KEY_TEACHER_NAME = "teacher_name"
        private const val KEY_TEACHER_EMAIL = "teacher_email"
    }

    var isLoggedIn: Boolean
        get() = prefs.getBoolean(KEY_IS_LOGGED_IN, false)
        private set(value) = prefs.edit().putBoolean(KEY_IS_LOGGED_IN, value).apply()

    var teacherId: Long
        get() = prefs.getLong(KEY_TEACHER_ID, -1L)
        private set(value) = prefs.edit().putLong(KEY_TEACHER_ID, value).apply()

    var teacherName: String
        get() = prefs.getString(KEY_TEACHER_NAME, "") ?: ""
        private set(value) = prefs.edit().putString(KEY_TEACHER_NAME, value).apply()

    var teacherEmail: String
        get() = prefs.getString(KEY_TEACHER_EMAIL, "") ?: ""
        private set(value) = prefs.edit().putString(KEY_TEACHER_EMAIL, value).apply()

    fun saveSession(id: Long, name: String, email: String) {
        prefs.edit()
            .putBoolean(KEY_IS_LOGGED_IN, true)
            .putLong(KEY_TEACHER_ID, id)
            .putString(KEY_TEACHER_NAME, name)
            .putString(KEY_TEACHER_EMAIL, email)
            .apply()
    }

    fun clearSession() {
        prefs.edit().clear().apply()
    }
}
