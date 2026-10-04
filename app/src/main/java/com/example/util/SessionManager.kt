package com.example.util

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("civic_session", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_USER_ROLE = "user_role"
        private const val KEY_WARD_CODE = "ward_code"
    }

    fun saveSession(userId: Int, name: String, email: String, role: String, wardCode: String) {
        prefs.edit().apply {
            putBoolean(KEY_IS_LOGGED_IN, true)
            putInt(KEY_USER_ID, userId)
            putString(KEY_USER_NAME, name)
            putString(KEY_USER_EMAIL, email)
            putString(KEY_USER_ROLE, role)
            putString(KEY_WARD_CODE, wardCode)
            apply()
        }
    }

    fun isLoggedIn(): Boolean = prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    fun getUserId(): Int = prefs.getInt(KEY_USER_ID, 3)
    fun getUserName(): String = prefs.getString(KEY_USER_NAME, "Ravi Kumar") ?: "Ravi Kumar"
    fun getUserEmail(): String = prefs.getString(KEY_USER_EMAIL, "citizen@civic.app") ?: "citizen@civic.app"
    fun getUserRole(): String = prefs.getString(KEY_USER_ROLE, "CITIZEN") ?: "CITIZEN"
    fun getWardCode(): String = prefs.getString(KEY_WARD_CODE, "CHN-W-001") ?: "CHN-W-001"

    fun isOfficerOrAdmin(): Boolean {
        val role = getUserRole().uppercase()
        return role == "OFFICER" || role == "ADMIN"
    }

    fun isAdmin(): Boolean = getUserRole().uppercase() == "ADMIN"

    fun logout() {
        prefs.edit().clear().apply()
    }
}
