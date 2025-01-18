package com.pin.recommend.util

import android.content.Context
import android.content.SharedPreferences

class PrefUtil(context: Context) {
    private val shPref: SharedPreferences = context.applicationContext.getSharedPreferences(
        context.packageName, Context.MODE_PRIVATE
    )
    private val editor: SharedPreferences.Editor = shPref.edit()

    fun getInt(key: String?): Int {
        return shPref.getInt(key, 0)
    }

    fun getLong(key: String?): Long {
        return shPref.getLong(key, 0L)
    }

    fun getBoolean(key: String?): Boolean {
        return shPref.getBoolean(key, false)
    }

    fun getString(key: String?): String {
        return shPref.getString(key, "") ?: ""
    }

    fun putInt(key: String?, value: Int) {
        editor.putInt(key, value).apply()
    }

    fun putLong(key: String?, value: Long) {
        editor.putLong(key, value).apply()
    }

    fun putBoolean(key: String?, value: Boolean) {
        editor.putBoolean(key, value).apply()
    }

    fun putString(key: String?, value: String?) {
        editor.putString(key, value).apply()
    }

    fun isEmpty(key: String?): Boolean {
        return getString(key).isEmpty()
    }

    @Synchronized
    fun remove(key: String?) {
        editor.remove(key).apply()
    }

    @Synchronized
    fun clear() {
        editor.clear().apply()
    }
}
