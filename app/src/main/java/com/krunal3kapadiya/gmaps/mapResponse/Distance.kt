package com.krunal3kapadiya.gmaps.mapResponse

import com.google.gson.annotations.SerializedName

data class Distance(@SerializedName("text")
                    val text: String,
                    @SerializedName("value")
                    val value: Long)