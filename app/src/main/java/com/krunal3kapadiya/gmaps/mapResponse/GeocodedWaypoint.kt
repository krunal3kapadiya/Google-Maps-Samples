package com.krunal3kapadiya.gmaps.mapResponse

import com.google.gson.annotations.SerializedName

data class GeocodedWaypoint(@SerializedName("geocoder_status")
                            val geocoderStatus: String,
                            @SerializedName("place_id")
                            val placeId: String,
                            @SerializedName("types")
                            val types: List<String>)