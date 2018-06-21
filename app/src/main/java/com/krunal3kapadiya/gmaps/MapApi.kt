package com.krunal3kapadiya.gmaps

import com.krunal3kapadiya.gmaps.mapResponse.PolyLineResponse
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface MapApi {
    @GET("/maps/api/directions/json")
    fun getDirection(@Query("origin") origin: String,
                     @Query("destination") destination: String,
                     @Query("key") key: String,
                     @Query("sensor") sensor: Boolean,
                     @Query("mode") mode: String): Observable<PolyLineResponse>
}