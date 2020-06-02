package com.krunal3kapadiya.gmaps

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng

class BackgroundLocationService : IntentService(BackgroundLocationService::class.java.simpleName) {

    companion object {
        private const val NOTIFICATION_ID = 101
        private var TAG: String = BackgroundLocationService::class.java.simpleName
        private var ARG_STOP_ACTION: String = "ARG_STOP_ACTION"

        fun stop(context: Context) {
            val intent = Intent(context, BackgroundLocationService::class.java)
            intent.action = ARG_STOP_ACTION
            context.startService(intent)
        }
    }

    private var firstLocation: LatLng? = null
    private var secondLocation: LatLng? = null
    lateinit var fusedLocationClient:FusedLocationProviderClient
    lateinit var locationCallback:LocationCallback
    lateinit var notificationManager: NotificationManagerCompat
    private val serviceBinder: IBinder = RunServiceBinder()

    inner class RunServiceBinder : Binder() {
        val service: BackgroundLocationService
            get() = this@BackgroundLocationService
    }

    fun initLocationUpdate(initialLatLang:LatLng) {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                for (location in locationResult.locations) {
                    // Update UI with location data
                    firstLocation = LatLng(location.latitude, location.longitude)
                }
            }
        }
        val locationRequest = LocationRequest.create()?.apply {
            interval = 1800000
            fastestInterval = 300000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ARG_STOP_ACTION) {
            Log.d(TAG, "onStartCommand -> STOP")
            fusedLocationClient.removeLocationUpdates(locationCallback)
            stopForeground(true)
            stopSelf()
        }else{
            startForeground(NOTIFICATION_ID, getNotificationBuilder()?.build())
        }
        return Service.START_NOT_STICKY
    }

    override fun onHandleIntent(intent: Intent?) {
    }

    override fun onBind(intent: Intent): IBinder? {
        return serviceBinder
    }

    private fun getNotificationBuilder(): NotificationCompat.Builder? {
        val channelId: String = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> {
                createNotificationChannel("my_service", "My Background Service")
            }
            else -> {
                ""
            }
        }
        val notificationIntent = Intent(this, MapsActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0,
            notificationIntent, 0
        )
        return NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentText(getString(R.string.backgrund_message))
            .setOngoing(true)
            .setContentIntent(pendingIntent)
    }

    private fun createNotificationChannel(
        channelId: String,
        channelName: String
    ): String {
        val chan = NotificationChannel(
            channelId,
            channelName, NotificationManager.IMPORTANCE_NONE
        )
        chan.lightColor = Color.BLUE
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        notificationManager =
            NotificationManagerCompat.from(this)
        notificationManager.createNotificationChannel(chan)
        return channelId
    }
}