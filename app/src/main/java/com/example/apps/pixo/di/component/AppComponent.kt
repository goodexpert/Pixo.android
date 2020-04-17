package com.example.apps.pixo.di.component

import com.example.apps.pixo.AppApplication
import com.example.apps.pixo.di.module.AppModule
import com.example.apps.pixo.ui.base.BaseActivity
import com.example.apps.pixo.ui.base.BaseFragment
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {
    fun inject(target: AppApplication)
    fun inject(target: BaseActivity)
    fun inject(target: BaseFragment)
}