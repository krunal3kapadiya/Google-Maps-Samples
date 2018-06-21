package com.krunal3kapadiya.gmaps.mapResponse

import com.google.gson.annotations.SerializedName

data class Polyline(@SerializedName("points")
                    var points: String)

