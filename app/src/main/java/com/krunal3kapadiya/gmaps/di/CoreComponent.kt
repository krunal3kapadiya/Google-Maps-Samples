package com.krunal3kapadiya.gmaps.di

import android.content.Context
import com.krunal3kapadiya.gmaps.MapApi
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, ApiModule::class])
interface CoreComponent {
    fun context(): Context

    fun mapApi(): MapApi
}