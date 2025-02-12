package com.blocker.storage

import android.content.Context
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule

class NativeLocalStorageModule(reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {

    override fun getName(): String {
        return "NativeLocalStorage"
    }

    @ReactMethod
    fun setItem(key: String, value: String) {
        val sharedPreferences = getReactApplicationContext().getSharedPreferences("Storage", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(key, value)
        editor.apply()
    }

    @ReactMethod
    fun getItem(key: String): String? {
        val sharedPreferences = getReactApplicationContext().getSharedPreferences("Storage", Context.MODE_PRIVATE)
        return sharedPreferences.getString(key, null)
    }

    @ReactMethod
    fun removeItem(key: String) {
        val sharedPreferences = getReactApplicationContext().getSharedPreferences("Storage", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove(key)
        editor.apply()
    }

    @ReactMethod
    fun clear() {
        val sharedPreferences = getReactApplicationContext().getSharedPreferences("Storage", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }
}
