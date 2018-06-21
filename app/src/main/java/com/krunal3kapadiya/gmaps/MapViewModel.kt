package com.krunal3kapadiya.gmaps

import android.annotation.SuppressLint
import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.ViewModel
import android.support.v4.content.ContextCompat
import android.util.Log
import com.google.android.gms.maps.model.*
import com.krunal3kapadiya.gmaps.di.Injector
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class MapViewModel : ViewModel() {

    @SuppressLint("StaticFieldLeak")
    val context = Injector.get().context()

    val errorMessage = MediatorLiveData<String>()
    val animateZoomCamera = MediatorLiveData<LatLngBounds>()
    val addMarker = MediatorLiveData<LatLng>()
    val clearMap = MediatorLiveData<Boolean>()
    val dayNightImageData = MediatorLiveData<Int>()
    val tvKmData = MediatorLiveData<String>()
    val resultFare = MediatorLiveData<String>()
    val destination = MediatorLiveData<String>()
    val duration = MediatorLiveData<String>()
    val addPolyLineOne = MediatorLiveData<PolylineOptions>()
    val distOne = MediatorLiveData<String>()

    val pathLocationOne = MediatorLiveData<ArrayList<LatLng>>()
    val pathLocationTwo = MediatorLiveData<ArrayList<LatLng>>()

    private lateinit var travellingTime1: String
    private lateinit var travellingTime2: String

    private lateinit var viaAddress1: String
    private lateinit var viaAddress2: String

    private lateinit var distance1: String
    private lateinit var distance2: String


    /**
     * use to decode poly line's latlang
     */
    fun decodePoly(encoded: String): List<LatLng> {
        val poly = ArrayList<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0

        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat

            shift = 0
            result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng

            val p = LatLng(lat.toDouble() / 1E5,
                    lng.toDouble() / 1E5)
            poly.add(p)
        }
        return poly
    }

    /**
     * animating map between two lat lang
     */
    fun setBoundsToMap(latLngArrayList: ArrayList<LatLng>) {
        when {
            latLngArrayList.size == 2 -> {
                val builder = LatLngBounds.Builder()
                builder.include(latLngArrayList[0])
                builder.include(latLngArrayList[1])
                animateZoomCamera.postValue(builder.build())
            }
        }
    }

    /**
     * fetching KM by distance and fare
     *
     */
    fun showResultData(km: String?, latLng: LatLng, latLngArrayList: ArrayList<LatLng>) {
        var type = 0
        if (latLngArrayList.contains(latLng)) {
            type = 1
        } else if (latLngArrayList.size == 2) {
            type = 2
        }
    }

    /**
     * this method will make api call for path between two distances and display it in map
     */
    fun makeApiCallForDirection(toDestList: ArrayList<LatLng>) {
        val mapApi = Injector.get().mapApi()
        val pathOneLocationArray = ArrayList<LatLng>()
        val pathTwoLocationArray = ArrayList<LatLng>()

        when {
            toDestList.size == 2 -> {
                val driving = mapApi.getDirection(
                        toDestList[0].latitude.toString().plus(",").plus(toDestList[0].longitude),
                        toDestList[1].latitude.toString().plus(",").plus(toDestList[1].longitude),
                        context.getString(R.string.maps_key),
                        true,
                        "driving")

                val walking = mapApi.getDirection(
                        toDestList[0].latitude.toString().plus(",").plus(toDestList[0].longitude),
                        toDestList[1].latitude.toString().plus(",").plus(toDestList[1].longitude),
                        context.getString(R.string.maps_key),
                        false,
                        "walking")

                Observable.concat(driving, walking).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            if (it.status == "OK") {
                                Log.e("Calling", "Inside observable path")
                                if (it.routes[0].legs[0].steps[0].travelMode == "DRIVING") {
                                    it.routes.forEach { routes ->
                                        distance1 = routes.legs[0].distance.text
                                        distOne.postValue(distance1)

                                        travellingTime1 = routes.legs[0].duration.text
                                        viaAddress1 = routes.summary
                                        routes.legs.forEach { leg ->
                                            leg.steps.forEach { steps ->
                                                val polyline = steps.polyline
                                                val points = polyline.points
                                                val polypts = decodePoly(points)
                                                pathOneLocationArray.addAll(polypts)
                                                pathLocationOne.postValue(pathOneLocationArray)
                                            }
                                        }
                                    }
                                    val lineOptions = PolylineOptions()
                                            .addAll(pathOneLocationArray)
                                            .color(ContextCompat.getColor(context, R.color.colorAccent))
                                            .width(7f)
                                            .geodesic(true)
                                    addPolyLineOne.postValue(lineOptions)
                                }
                            }

                        }, {
                            Log.e("Map Directions", it.message)
                        })
            }
            else -> {
                errorMessage.postValue("Please enter destination point")
            }
        }
    }
}