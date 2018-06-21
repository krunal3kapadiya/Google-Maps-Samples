package com.krunal3kapadiya.gmaps.mapResponse

import com.google.gson.annotations.SerializedName

class PolyLineResponse(
        @SerializedName("geocoded_waypoints")
        var geocodedWaypoints: List<GeocodedWaypoint>,
        @SerializedName("routes")
        var routes: List<Route>,
        @SerializedName("status")
        var status: String)