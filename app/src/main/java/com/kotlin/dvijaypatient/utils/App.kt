package com.kotlin.dvijaypatient.utils;
import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.FirebaseApp

    class App : Application() {
        override fun onCreate() {
            super.onCreate()
            FirebaseApp.initializeApp(this) // ✅ initialize once for the whole app
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }