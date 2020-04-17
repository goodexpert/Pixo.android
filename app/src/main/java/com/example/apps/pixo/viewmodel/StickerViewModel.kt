package com.example.apps.pixo.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.apps.pixo.R

class StickerViewModel : AndroidViewModel {
    companion object {
        private val stickerList: List<Int> by lazy {
            listOf(
                R.drawable.ic_sticker_001, R.drawable.ic_sticker_002, R.drawable.ic_sticker_003,
                R.drawable.ic_sticker_004, R.drawable.ic_sticker_005, R.drawable.ic_sticker_006,
                R.drawable.ic_sticker_007, R.drawable.ic_sticker_008, R.drawable.ic_sticker_009,
                R.drawable.ic_sticker_010, R.drawable.ic_sticker_011, R.drawable.ic_sticker_012,
                R.drawable.ic_sticker_013, R.drawable.ic_sticker_014
            )
        }

        private var singletonInstance: StickerViewModel? = null

        @Synchronized
        fun getInstance(application: Application): StickerViewModel {
            if (singletonInstance == null) {
                singletonInstance = StickerViewModel(application)
            }
            return singletonInstance!!
        }
    }

    val stickersLiveData: LiveData<List<Int>> by lazy { MutableLiveData<List<Int>>(stickerList) }

    private constructor(application: Application): super(application) {
    }
}