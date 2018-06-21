package com.krunal3kapadiya.gmaps.mapResponse

import com.google.gson.annotations.SerializedName

data class Bounds(@SerializedName("northeast")
                  val northeast: Northeast,
                  @SerializedName("southwest")
                  val southwest: SouthWest)