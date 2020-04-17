package com.example.apps.pixo.ui.base

import android.annotation.TargetApi
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.apps.pixo.di.component.AppComponent
import com.example.apps.pixo.util.AndroidDisposable
import com.example.apps.pixo.viewmodel.ViewModelProviderFactory
import javax.inject.Inject

abstract class BaseFragment : Fragment() {
    private val androidDisposable: AndroidDisposable by lazy { AndroidDisposable() }
    private var fragmentInterface: OnFragmentInterface? = null

    @Inject
    lateinit var providerFactory: ViewModelProviderFactory

    abstract val layoutResourceId: Int

    fun getDisposable(): AndroidDisposable {
        return androidDisposable
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            fragmentInterface = context as OnFragmentInterface
        } catch (ex: ClassCastException) {
            throw NotImplementedError("OnFragmentInterface not implemented")
        }
    }

    override fun onDetach() {
        fragmentInterface?.let {
            fragmentInterface = null
        }
        super.onDetach()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent()?.inject(this)
    }

    override fun onDestroy() {
        getDisposable().dispose()
        super.onDestroy()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(layoutResourceId, container, false)
    }

    open fun getRequiredPermissions(): Array<String> = emptyArray()

    fun hasPermissions() = getRequiredPermissions().all {
        ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
    }

    @TargetApi(Build.VERSION_CODES.M)
    fun hasPermission(permission: String): Boolean {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M ||
                ContextCompat.checkSelfPermission(activity!!, permission) == PackageManager.PERMISSION_GRANTED
    }

    @TargetApi(Build.VERSION_CODES.M)
    fun requestPermissionsSafely(permissions: Array<String>, requestCode: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, requestCode)
        }
    }

    fun appComponent(): AppComponent? {
        return fragmentInterface?.appComponent()
    }

    fun postAction(action: Runnable) {
        fragmentInterface?.postAction(action)
    }

    fun postAction(action: Runnable, delayMillis: Long) {
        fragmentInterface?.postAction(action, delayMillis)
    }

    fun popBackStack() {
        fragmentInterface?.popBackStack()
    }

    fun showActionBar() {
        fragmentInterface?.showActionBar()
    }

    fun hideActionBar() {
        fragmentInterface?.hideActionBar()
    }

    fun setTitle(resId: Int) {
        fragmentInterface?.setTitle(resId)
    }

    fun setTitle(title: CharSequence?) {
        fragmentInterface?.setTitle(title)
    }
}

interface OnFragmentInterface {
    fun appComponent(): AppComponent

    fun postAction(action: Runnable)
    fun postAction(action: Runnable, delayMillis: Long)

    fun popBackStack()

    fun showActionBar()
    fun hideActionBar()

    fun setTitle(resId: Int)
    fun setTitle(title: CharSequence?)
}