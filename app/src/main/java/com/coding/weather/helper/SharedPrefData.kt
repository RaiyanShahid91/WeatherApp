package com.coding.weather.helper

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.util.Log
import com.coding.weather.helper.Constants.APPDATA
import com.coding.weather.helper.Constants.SAVEDCITY

class SharedPrefData(context: Context) {

    var sharedPreferences: SharedPreferences = context.getSharedPreferences(APPDATA, MODE_PRIVATE)
    private var myDataEdit = sharedPreferences.edit()

    fun saveDataString(key: String, value: String) {
        myDataEdit.putString(key, value)
        myDataEdit.commit()
        Log.d(key, value)
    }

    fun saveDataInt(key: String, value: Int) {
        myDataEdit.putInt(key, value)
        myDataEdit.commit()
    }

    fun getDataString(key: String): String {
        Log.d(key, sharedPreferences.getString(SAVEDCITY, "").toString())
        return sharedPreferences.getString(SAVEDCITY, "").toString()
    }

    fun getDataInt(key: String): Int {
        return sharedPreferences.getInt(SAVEDCITY, 0)
    }
}