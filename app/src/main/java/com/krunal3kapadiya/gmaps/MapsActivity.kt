package com.krunal3kapadiya.gmaps

import android.annotation.SuppressLint
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.places.PlaceBuffer
import com.google.android.gms.location.places.Places
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_maps.*
import kotlinx.android.synthetic.main.info_window_layout.view.*
import java.util.*


class MapsActivity : FragmentActivity(),
        OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks {

    companion object {
        fun launch(context: Context) {
            val intent = Intent(context, MapsActivity::class.java)
            context.startActivity(intent)
        }
    }

    private var map: GoogleMap? = null
    private var toDestList: ArrayList<LatLng>? = null
    private var pathOneLocationArray: ArrayList<LatLng>? = null
    private var pathTwoLocationArray: ArrayList<LatLng>? = null
    private var markerArrayList: ArrayList<Marker>? = null
    private var adapter: PlaceAutocompleteAdapter? = null
    var selectedPath = 1

    var currentLatLng: LatLng = LatLng(23.0225, 72.5714)

    private var distance1: String? = null
    lateinit var viewModel: MapViewModel

    private var locationRequest: LocationRequest? = null


    override fun onConnectionSuspended(p0: Int) {}

    private lateinit var googleApiClient: GoogleApiClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_maps)

        googleApiClient = GoogleApiClient.Builder(this@MapsActivity)
                .addConnectionCallbacks(this)
                .enableAutoManage(this@MapsActivity, 0 /* clientId */, this)
                .addApi(Places.GEO_DATA_API)
                .addApi(LocationServices.API)
                .build()

        toDestList = ArrayList()
        markerArrayList = ArrayList()
        pathOneLocationArray = ArrayList()
        pathTwoLocationArray = ArrayList()

        viewModel = ViewModelProviders.of(this).get(MapViewModel::class.java)

        mapView.onCreate(savedInstanceState)
        mapView.onResume()
        mapView.getMapAsync(this)

        activity_maps_bt_submit.setOnClickListener {
            viewModel.makeApiCallForDirection(toDestList!!)
            viewModel.setBoundsToMap(toDestList!!)
            viewModel.pathLocationOne.observe(this@MapsActivity, android.arch.lifecycle.Observer { pathOneLocationArray?.addAll(it!!) })
            viewModel.pathLocationTwo.observe(this@MapsActivity, android.arch.lifecycle.Observer { pathTwoLocationArray?.addAll(it!!) })
        }

        adapter = PlaceAutocompleteAdapter(this@MapsActivity, googleApiClient, LatLngBounds(currentLatLng, currentLatLng), null)

        et_location_one.setAdapter<PlaceAutocompleteAdapter>(adapter)
        et_location_two.setAdapter<PlaceAutocompleteAdapter>(adapter)

        et_location_one.onItemClickListener = autocompleteClickListener
        et_location_one.onItemClickListener = autocompleteClickListener
    }


    override fun onConnected(p0: Bundle?) {}
    override fun onConnectionFailed(p0: ConnectionResult) {}

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                super.onLocationResult(locationResult)
                locationResult?.lastLocation?.let { map?.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(it.latitude, it.longitude), 10f)) }
            }
        }
        val v = layoutInflater.inflate(R.layout.info_window_layout, null)
        viewModel.animateZoomCamera.observe(this, android.arch.lifecycle.Observer { map?.animateCamera(CameraUpdateFactory.newLatLngBounds(it, 16)) })
        viewModel.addMarker.observe(this, android.arch.lifecycle.Observer { map?.addMarker(MarkerOptions().position(it!!)) })
        viewModel.clearMap.observe(this, android.arch.lifecycle.Observer { if (it!!) map?.clear() })
        viewModel.addPolyLineOne.observe(this, android.arch.lifecycle.Observer {
            val polyLine = map?.addPolyline(it)
            polyLine?.isClickable = true
            polyLine?.tag = "ONE"
        })

        map?.setInfoWindowAdapter(object : GoogleMap.InfoWindowAdapter {

            // Use default InfoWindow frame
            override fun getInfoWindow(arg0: Marker): View? {
                return null
            }

            // Defines the contents of the InfoWindow
            override fun getInfoContents(arg0: Marker): View? {
                val latLng = arg0.position

                var isViewNotNull = false//check for the view

                viewModel.showResultData(distance1?.replace("km", "")?.trim({ it <= ' ' }), latLng, toDestList!!)
                viewModel.distOne.observe(this@MapsActivity, android.arch.lifecycle.Observer { distance1 = it })
                viewModel.tvKmData.observe(this@MapsActivity, android.arch.lifecycle.Observer { v.tvMarkerSmartResultKM.text = it })
                viewModel.destination.observe(this@MapsActivity, android.arch.lifecycle.Observer {
                    v.tvMarkerDestinationVia.text = it
                    isViewNotNull = true
                })
                viewModel.duration.observe(this@MapsActivity, android.arch.lifecycle.Observer { v.tvMarkerTimeDuration.text = it })
                viewModel.errorMessage.observe(this@MapsActivity, android.arch.lifecycle.Observer {
                    Toast.makeText(this@MapsActivity, it, Toast.LENGTH_SHORT).show()
                })

                return when {
                    isViewNotNull -> v
                    else -> null
                }
            }
        })


    }

    /**
     * autocompletion suggetion in map
     */
    private val autocompleteClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
        val item = adapter!!.getItem(position)
        val placeId = item!!.placeId
        val primaryText = item.getPrimaryText(null)
        val placeResult = Places.GeoDataApi
                .getPlaceById(googleApiClient, placeId)
        placeResult.setResultCallback(ResultCallback<PlaceBuffer> { places ->
            if (!places.status.isSuccess) {
                places.release()
                return@ResultCallback
            }
            val place = places.get(0)
            toDestList!!.add(place.latLng)
            map?.addMarker(MarkerOptions().position(place.latLng).title(place.name.toString()))?.let { markerArrayList!!.add(it) }
            map?.moveCamera(CameraUpdateFactory.newLatLng(place.latLng))
            places.release()
        })
    }
}