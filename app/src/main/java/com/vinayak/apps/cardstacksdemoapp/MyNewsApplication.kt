package com.vinayak.apps.cardstacksdemoapp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.WorkManager
import javax.inject.Inject

const val CHANNEL = "channel"
const val NAME = "name"

@HiltAndroidApp
class MyNewsApplication: Application() {

//    @Inject
//    lateinit var hiltWorkerFactory: HiltWorkerFactory

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()
//        WorkManager.initialize(this,Configuration.Builder()
//            .setWorkerFactory(hiltWorkerFactory).build())

        checkSdkVersion { isGreaterThanOreo ->
            if(isGreaterThanOreo) {
                val notificationChannel = NotificationChannel(CHANNEL, NAME,NotificationManager.IMPORTANCE_HIGH)
                val notificationManager = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(
                    notificationChannel
                )
            }
        }

    }
}

private inline fun checkSdkVersion(isGreaterThanOreo: (Boolean) -> Unit) {
    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) isGreaterThanOreo(true)
    else isGreaterThanOreo(false)
}