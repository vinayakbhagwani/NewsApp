package com.vinayak.apps.cardstacksdemoapp.utils

import android.content.SharedPreferences
import java.util.concurrent.TimeUnit

object Helpers {

    const val API_TIME = "api_time"
    const val TOTAL_MINUTES = 240

    fun verifyIfDbDeleteRequired(sharedPreferences: SharedPreferences): Int {
        val currentTime = System.currentTimeMillis()
        return if (sharedPreferences.contains(API_TIME)) {
            val initialTimeMillis = sharedPreferences.getLong(API_TIME, 0L)
            val diffTimeMillis = currentTime - initialTimeMillis
            val minutes = TimeUnit.MILLISECONDS.toMinutes(diffTimeMillis).toInt()
            minutes
        } else {
            sharedPreferences.edit().putLong(API_TIME, currentTime).apply()
            0
        }
    }

}