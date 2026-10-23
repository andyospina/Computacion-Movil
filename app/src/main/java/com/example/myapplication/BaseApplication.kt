package com.example.myapplication

import android.app.Application
import com.example.myapplication.notifications.AppNotifications
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class BaseApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        AppNotifications.createChannels(this)
    }
}
