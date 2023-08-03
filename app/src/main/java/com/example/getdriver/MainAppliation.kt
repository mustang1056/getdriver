package com.example.getdriver

import android.app.Application
import com.yandex.mapkit.MapKitFactory
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class MainApplication : Application() {

    private val MAPKIT_API_KEY = "2296da6d-ba53-468f-817b-ec68e0cb1e2c"


    override fun onCreate() {
        super.onCreate()
        // Set the api key before calling initialize on MapKitFactory.
        MapKitFactory.setApiKey(MAPKIT_API_KEY)


        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

}