package com.example.apps.pixo.ui.splash

import android.Manifest
import android.content.Intent
import android.os.Bundle
import com.example.apps.pixo.ui.main.MainActivity
import com.example.apps.pixo.R
import com.example.apps.pixo.ui.base.BaseActivity
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class SplashActivity : BaseActivity() {
    companion object {
        private const val TAG = "SplashActivity"
        private const val PERMISSIONS_REQUEST_CODE = 1000

        private val PERMISSIONS_REQUIRED = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        if (!hasPermissions()) {
            requestPermissionsSafely(PERMISSIONS_REQUIRED, PERMISSIONS_REQUEST_CODE)
        } else {
            onNextScreen()
        }
    }

    override fun getRequiredPermissions(): Array<String> =
        PERMISSIONS_REQUIRED

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            if (!hasPermissions()) {
                finishAffinity()
            } else {
                onNextScreen()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun onNextScreen() {
        getDisposable().add(
            Observable.timer(1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    startActivity(Intent(this, MainActivity::class.java).apply {
                        flags += Intent.FLAG_ACTIVITY_CLEAR_TASK and Intent.FLAG_ACTIVITY_NEW_TASK
                    })
                    finish()
                })
    }
}
