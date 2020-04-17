package com.example.apps.pixo.ui.base

import android.annotation.TargetApi
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.apps.pixo.AppApplication
import com.example.apps.pixo.di.component.AppComponent
import com.example.apps.pixo.util.AndroidDisposable
import com.example.apps.pixo.viewmodel.ViewModelProviderFactory
import javax.inject.Inject

abstract class BaseActivity : AppCompatActivity(), OnFragmentInterface {
    private val androidDisposable: AndroidDisposable by lazy { AndroidDisposable() }

    @Inject
    lateinit var providerFactory: ViewModelProviderFactory

    fun getDisposable(): AndroidDisposable {
        return androidDisposable
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent().inject(this)
    }

    override fun onDestroy() {
        getDisposable().dispose()
        super.onDestroy()
    }

    open fun getRequiredPermissions(): Array<String> = emptyArray()

    fun hasPermissions() = getRequiredPermissions().all {
        ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
    }

    @TargetApi(Build.VERSION_CODES.M)
    fun hasPermission(permission: String): Boolean {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M ||
                checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
    }

    @TargetApi(Build.VERSION_CODES.M)
    fun requestPermissionsSafely(permissions: Array<String>, requestCode: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, requestCode)
        }
    }

    override fun appComponent(): AppComponent {
        return (applicationContext as AppApplication).appComponent
    }

    override fun postAction(action: Runnable) {
        if (isFinishing) return
        (application as AppApplication).postAction(action)
    }

    override fun postAction(action: Runnable, delayMillis: Long) {
        if (isFinishing) return
        (application as AppApplication).postAction(action, delayMillis)
    }

    override fun popBackStack() {
        onBackPressed()
    }

    override fun showActionBar() {
        supportActionBar?.show()
    }

    override fun hideActionBar() {
        supportActionBar?.hide()
    }

    override fun setTitle(resId: Int) {
        supportActionBar?.setTitle(resId)
    }

    override fun setTitle(title: CharSequence?) {
        supportActionBar?.title = title
    }
}