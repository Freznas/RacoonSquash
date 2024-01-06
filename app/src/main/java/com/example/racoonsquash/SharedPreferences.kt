package com.example.racoonsquash

import android.app.Application

class SharedPreferences : Application() {
    lateinit var sharedPreferencesManager: SharedPreferencesManager

    override fun onCreate() {
        super.onCreate()
        sharedPreferencesManager = SharedPreferencesManager(this)
    }
}