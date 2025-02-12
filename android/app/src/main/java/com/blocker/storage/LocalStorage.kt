package com.blocker.storage

import android.content.Context
import com.google.gson.Gson

class LocalStorage(private val context: Context) {

    private val sharedPreferences = context.getSharedPreferences("Storage", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun setItem(key: String, value: String) {
        val editor = sharedPreferences.edit()
        editor.putString(key, value)
        editor.apply()
    }

  
    fun getItem(key: String): String? {
        return sharedPreferences.getString(key, null)
    }

    fun removeItem(key: String) {
        val editor = sharedPreferences.edit()
        editor.remove(key)
        editor.apply()
    }

    fun clear() {
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }

 
}
