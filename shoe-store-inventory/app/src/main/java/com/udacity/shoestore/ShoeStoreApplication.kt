package com.udacity.shoestore

import android.app.Application
import timber.log.Timber

class ShoeStoreApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }
}