package com.example.racoonsquash

import android.app.Application

class MyApp : Application() {
    lateinit var sharedPreferencesManager: SharedPreferencesManager

    override fun onCreate() {
        super.onCreate()
        sharedPreferencesManager = SharedPreferencesManager(this)
    }
}