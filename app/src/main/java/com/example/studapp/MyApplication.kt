package com.example.studapp

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log


const val sharedName: String = "sharedData.data"
const val frequencyString: String = "frequency"

class MyApplication : Application() {
    lateinit var sharedPref: SharedPreferences
    override fun onCreate() {
        super.onCreate()

        initShared()
    }

    private fun initShared() {
        sharedPref = getSharedPreferences(sharedName, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPref.edit()
        if (!sharedPref.contains(frequencyString)) {
            try {
                editor.putFloat(frequencyString, 10f)
                editor.apply()
            } catch (ex: Exception) {
                Log.i("SharedPref", ex.message.toString())
            }
        }
    }

}