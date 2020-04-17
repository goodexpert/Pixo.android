package com.example.apps.pixo

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.os.Handler
import android.os.Looper
import com.example.apps.pixo.di.component.AppComponent
import com.example.apps.pixo.di.component.DaggerAppComponent
import com.example.apps.pixo.di.module.AppModule
import com.example.apps.pixo.util.VideoRequestHandler
import com.squareup.picasso.LruCache
import com.squareup.picasso.OkHttp3Downloader
import com.squareup.picasso.Picasso
import okhttp3.Cache
import okhttp3.OkHttpClient

class AppApplication : Application(), AppInterface {
    companion object {
        private val TAG = "AppApplication"
    }

    val appComponent: AppComponent by lazy {
        DaggerAppComponent.builder()
            .appModule(AppModule(this))
            .build()
    }

    override fun onCreate() {
        super.onCreate()
        appComponent.inject(this)

        // Initialize piccaso
        initializePicasso(this)
    }

    override fun getApplication(): Application = this

    override fun postAction(action: Runnable) {
        Handler(Looper.getMainLooper()).post(action)
    }

    override fun postAction(action: Runnable, delayMillis: Long) {
        Handler(Looper.getMainLooper()).postDelayed(action, delayMillis)
    }

    override fun getSharedPreferences(): SharedPreferences =
        getSharedPreferences(packageName, Context.MODE_PRIVATE)

    private fun initializePicasso(context: Context) {
        // create a OkHttp 3 downloader
        val cache = Cache(context.cacheDir, 1_000_000)
        val client = OkHttpClient.Builder()
            .cache(cache)
            .build()

        // create Picasso.Builder object
        val picassoBuilder = Picasso.Builder(context)
            .downloader(OkHttp3Downloader(client))
            .memoryCache(LruCache(100_000_000))
            .addRequestHandler(VideoRequestHandler(this))

        // Picasso.Builder creates the Picasso object to do the actual requests
        val picasso = picassoBuilder.build().apply {
            isLoggingEnabled = true
            setIndicatorsEnabled(false)
        }

        // set the global instance to use this Picasso object
        // all following Picasso (with Picasso.with(Context context) requests will use this Picasso object
        // you can only use the setSingletonInstance() method once!
        try {
            Picasso.setSingletonInstance(picasso)
        } catch (ignored: IllegalStateException) {
            // Picasso instance was already set
            // cannot set it after Picasso.with(Context) was already in use
        }
    }
}