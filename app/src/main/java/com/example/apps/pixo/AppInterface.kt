package com.example.apps.pixo

import android.app.Application
import android.content.SharedPreferences

interface AppInterface {
    fun getApplication(): Application
    fun getSharedPreferences(): SharedPreferences

    fun postAction(action: Runnable)
    fun postAction(action: Runnable, delayMillis: Long)
}