package com.krunal3kapadiya.gmaps

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val permission = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)

        if ((ContextCompat.checkSelfPermission(this, permission[0])) +
                (ContextCompat.checkSelfPermission(this, permission[1]))
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, permission, PERMISSION_REQUEST_CODE);
        } else {
            callMethodToRunTask();
        }

    }

    private val PERMISSION_REQUEST_CODE: Int = 1

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_REQUEST_CODE ->
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] + grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    callMethodToRunTask()
                }
        }
    }

    private fun callMethodToRunTask() {
        MapsActivity.launch(this)
        finish()
    }
}