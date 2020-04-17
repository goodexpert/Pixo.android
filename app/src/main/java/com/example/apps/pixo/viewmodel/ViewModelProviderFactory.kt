package com.example.apps.pixo.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ViewModelProviderFactory : ViewModelProvider.NewInstanceFactory {
    private val application: Application

    @Inject
    constructor(application: Application) : super() {
        this.application = application
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AlbumViewModel::class.java) -> {
                AlbumViewModel(application) as T
            }
            modelClass.isAssignableFrom(MediaViewModel::class.java) -> {
                MediaViewModel(application) as T
            }
            modelClass.isAssignableFrom(StickerViewModel::class.java) -> {
                StickerViewModel.getInstance(application) as T
            }
            else -> {
                throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
            }
        }
    }
}