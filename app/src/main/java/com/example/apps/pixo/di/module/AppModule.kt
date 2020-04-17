package com.example.apps.pixo.di.module

import android.app.Application
import android.content.SharedPreferences
import com.example.apps.pixo.AppApplication
import com.example.apps.pixo.AppInterface
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule(private val application: AppApplication) {

    @Singleton
    @Provides
    fun provideApplication(): Application =
        application

    @Singleton
    @Provides
    fun provideAppInterface(): AppInterface =
        application

    @Singleton
    @Provides
    fun provideSharedPreferences(): SharedPreferences =
        application.getSharedPreferences()
}