package com.krunal3kapadiya.gmaps.di

import com.krunal3kapadiya.gmaps.GMapApplication

class Injector private constructor() {
    companion object {
        fun get(): CoreComponent =
                GMapApplication.INSTANCE.component
    }
}
