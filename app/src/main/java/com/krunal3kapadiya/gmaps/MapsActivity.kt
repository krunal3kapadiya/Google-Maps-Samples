package com.krunal3kapadiya.gmaps

import android.Manifest
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.*
import com.google.android.gms.location.places.Places
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import kotlinx.android.synthetic.main.activity_maps.*
import java.util.*


class MapsActivity : AppCompatActivity(),
        OnMapReadyCallback,
        LocationListener,
        OnCompleteListener<Void>,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    val TAG = MapsActivity::class.java.simpleName

    companion object {
        fun launch(context: Context) {
            val intent = Intent(context, MapsActivity::class.java)
            context.startActivity(intent)
        }
        val ARG_IS_ENTERED_IN_REGION = "is_entered_in_region"

        @JvmStatic
        fun launch(context: Context, enterdInRegion: Boolean): Intent {
            val intent = Intent(context, MapsActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra(ARG_IS_ENTERED_IN_REGION, enterdInRegion)
            return intent
        }
    }


    override fun onConnected(p0: Bundle?) {
        locationRequest = LocationRequest()
        locationRequest!!.interval = 1000
        locationRequest!!.fastestInterval = 1000
        locationRequest!!.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation.addOnSuccessListener(this) {
                map!!.addMarker(MarkerOptions().position(LatLng(it.latitude, it.longitude)))
                map!!.moveCamera(CameraUpdateFactory.newLatLng(LatLng(it.latitude, it.longitude)))
            }
        }
    }

    private var locationRequest: LocationRequest? = null


    override fun onConnectionSuspended(p0: Int) {}

    private lateinit var googleApiClient: GoogleApiClient
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var map: GoogleMap? = null
    private var latLngArrayList: ArrayList<LatLng>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_maps)

        geofencingClient = LocationServices.getGeofencingClient(this)

        buildGoogleApiClient()

        checkPermissions()

        latLngArrayList = ArrayList()

        activity_maps_bt_submit.setOnClickListener {}

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)


        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


        fab_change_map.setOnClickListener {
            val mapsType = resources.getStringArray(R.array.types_of_map)

            AlertDialog.Builder(this).setTitle("Map Types")
                    .setItems(mapsType) { _, which ->
                        when (which) {
                            0 -> {
                                map?.mapType = GoogleMap.MAP_TYPE_SATELLITE
                            }
                            1 -> {
                                map?.mapType = GoogleMap.MAP_TYPE_NORMAL
                            }
                            2 -> {
                                map?.mapType = GoogleMap.MAP_TYPE_TERRAIN
                            }
                            3 -> {
                                map?.mapType = GoogleMap.MAP_TYPE_HYBRID
                            }
                        }
                    }.create().show()
        }
    }

    private val PERMISSIONS_REQUEST_LOCATION: Int = 100

    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(this@MapsActivity,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this@MapsActivity,
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            if (ActivityCompat.shouldShowRequestPermissionRationale(this@MapsActivity,
                            Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this@MapsActivity,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                        PERMISSIONS_REQUEST_LOCATION)
            }
        } else {
            // Permission has already been granted
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_about-> AboutUsActivity.launch(this)
        }
        return true
    }
    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSIONS_REQUEST_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return
            }

            // Add other 'when' lines to check for other
            // permissions this app might request.
            else -> {
                // Ignore all other requests.
            }
        }
    }


    private fun buildGoogleApiClient() {
        googleApiClient = GoogleApiClient.Builder(this)
                .enableAutoManage(this, 0 /* clientId */, this)
                .addApi(Places.GEO_DATA_API)
                .build()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        if (ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return
        } else {
            map = googleMap
            // this will add blue icon in map
            map?.isMyLocationEnabled = true
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            fusedLocationClient.lastLocation
                    .addOnSuccessListener { location: Location? ->
                        // Got last known location. In some rare situations this can be null.
                        location?.let {
                            map?.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(location.latitude, location.longitude), 16F))
                            addGeoFencing(LatLng(location.latitude, location.longitude))
                            startService()
                            val address = Geocoder(this, Locale.getDefault()).getFromLocation(
                                location.latitude,
                                location.longitude,
                                1
                            )
                            Log.d(TAG, "address  = ".plus(address[0]))
                        }
                    }

        }
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        Toast.makeText(this,
                "Could not connect to Google API Client: Error " + connectionResult.errorCode,
                Toast.LENGTH_SHORT).show()
    }

    override fun onLocationChanged(location: Location) {
    }
    lateinit var backGroundLocationUpdateService : BackgroundLocationService
    var serviceBound = false

    private val connection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            Log.d(TAG, "Service bound")
            val binder =
                service as BackgroundLocationService.RunServiceBinder
            backGroundLocationUpdateService = binder.service
            serviceBound = true
//            calculateDistanceService.initLocationUpdate(initialLatLang)
        }

        override fun onServiceDisconnected(name: ComponentName) {
            Log.d(TAG, "Service disconnect")
            serviceBound = false
        }
    }

    private lateinit var geofencingClient: GeofencingClient
    private var geofencePendingIntent: PendingIntent? = null

    private fun addGeoFencing(latlang: LatLng) {
        geofencingClient.addGeofences(getGeofencingRequest(latlang), getGeofencePendingIntent(this))
            ?.addOnCompleteListener(this@MapsActivity)
    }

    private fun removeGeofences() {
        geofencingClient.removeGeofences(getGeofencePendingIntent(this))
            ?.addOnCompleteListener(this)
    }

    private fun getGeofencePendingIntent(context: Context?): PendingIntent? {
        // Reuse the PendingIntent if we already have it.
        if (geofencePendingIntent != null) {
            return geofencePendingIntent
        }
        val intent =
            Intent(context, GeofenceBroadcastReceiver::class.java)
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // addGeofences() and removeGeofences().
        geofencePendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        return geofencePendingIntent
    }

    fun getGeofencingRequest(initialLatLang: LatLng): GeofencingRequest? {
        val builder = GeofencingRequest.Builder()

        // The INITIAL_TRIGGER_ENTER flag indicates that geofencing service should trigger a
        // GEOFENCE_TRANSITION_ENTER notification when the geofence is added and if the device
        // is already inside that geofence.
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_EXIT)

        // Add the geofences to be monitored by geofencing service.
        val mGeofenceList: ArrayList<Geofence>? = ArrayList()
        mGeofenceList?.add(
            Geofence.Builder()
                .setRequestId(getString(R.string.app_name))
                .setCircularRegion(
                    initialLatLang.latitude,
                    initialLatLang.longitude,
                    15F
                )
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(
                    Geofence.GEOFENCE_TRANSITION_ENTER or
                            Geofence.GEOFENCE_TRANSITION_EXIT
                )
                .build()
        )

        builder.addGeofences(mGeofenceList)

        // Return a GeofencingRequest.
        return builder.build()
    }

    fun startService() {
        val i = Intent(this, BackgroundLocationService::class.java)
        startService(i)
        bindService(i, connection, 0)
    }

    fun stopService() {
        Log.d(TAG, "On Stop Service Called")
        BackgroundLocationService.stop(this)
    }

    override fun onComplete(task: Task<Void>) {

    }
}