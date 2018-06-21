package com.krunal3kapadiya.gmaps.mapResponse

import com.google.gson.annotations.SerializedName

class StartLocation(@SerializedName("lat")
                    var lat: Double,
                    @SerializedName("lng")
                    var lng: Double)