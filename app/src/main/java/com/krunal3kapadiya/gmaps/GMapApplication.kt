package com.krunal3kapadiya.gmaps

import android.content.Context
import android.support.multidex.MultiDex
import android.support.multidex.MultiDexApplication

import com.krunal3kapadiya.gmaps.di.AppModule
import com.krunal3kapadiya.gmaps.di.CoreComponent
import com.krunal3kapadiya.gmaps.di.*

class GMapApplication : MultiDexApplication() {

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)

        INSTANCE = this
        component = DaggerCoreComponent.builder()
                .appModule(AppModule(this))
                .build()
    }

    lateinit var component: CoreComponent private set

    companion object {
        @JvmStatic
        lateinit var INSTANCE: GMapApplication
            private set
    }
}