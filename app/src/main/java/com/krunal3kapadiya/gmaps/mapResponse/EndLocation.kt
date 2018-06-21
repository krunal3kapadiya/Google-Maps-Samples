package com.krunal3kapadiya.gmaps.mapResponse

import com.google.gson.annotations.SerializedName

data class EndLocation(@SerializedName("lat")
                       private val lat: Double,
                       @SerializedName("lng")
                       private val lng: Double)