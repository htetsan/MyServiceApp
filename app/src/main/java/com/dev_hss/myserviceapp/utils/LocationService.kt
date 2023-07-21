package com.dev_hss.myserviceapp.utils

import android.Manifest
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.dev_hss.myserviceapp.MainActivity


class LocationService : Service() {

    companion object {
        val ACTION_START: String = "action_start"
        val ACTION_STOP: String = "action_stop"
        private const val PERMISSION_REQUEST_ACTION = "permission_request_action"
        const val PERMISSION_RESULT_ACTION = "permission_result_action"
        const val EXTRA_PERMISSION_GRANTED = "extra_permission_granted"
        // ...
    }

    private val REQUEST_CODE: Int = 101
    private val MIN_TIME_BETWEEN_UPDATES: Long = 5000 //5s
    private val MIN_DISTANCE_CHANGE_FOR_UPDATES: Float = 0.2f //1f means 1meter
    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: LocationListener

    private val permissionReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == PERMISSION_RESULT_ACTION) {
                val granted = intent.getBooleanExtra(EXTRA_PERMISSION_GRANTED, false)
                if (granted) {
                    startLocationUpdates()
                } else {
                    // Permission denied, handle accordingly
                    showMessage("Permission denied")
                }
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        Log.d("TAG", "onBind: reach on bind")
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Retrieve the action from the intent
        val action = intent?.action

        // Handle different actions
        when (action) {
            ACTION_START -> {
                startLocationUpdates()
            }

            ACTION_STOP -> {
                // Perform cleanup or stop tasks
                stopSelf()
            }
            // Handle other actions as needed
        }

        // Return a value indicating how the service should behave in case it gets terminated
        return START_STICKY
        //return super.onStartCommand(intent, flags, startId);
    }


    override fun onCreate() {
        super.onCreate()

        // Initialize LocationManager
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        // Register the broadcast receiver for permission result
        Log.d("TAG", "LocationService: __________________")

        // Initialize LocationListener
        locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                // Handle new location updates here
                Log.d("TAG", "LocationService:location $location")
                // Handle new location updates here
                location.let {
                    val latitude = location.latitude
                    val longitude = location.longitude
                    // Process location data as needed
                    Log.d("TAG", "LocationService:latitude {$latitude and $longitude}")
                    //showMessage("{$latitude and $longitude}")
                }
            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
                showMessage("$provider $status ${extras.toString()}" )

                // Handle status changes if needed
            }

            override fun onProviderEnabled(provider: String) {
                showMessage(provider)

                // Handle provider enable if needed
            }

            override fun onProviderDisabled(provider: String) {
                showMessage(provider)

                // Handle provider disable if needed
            }
        }


        // Initialize LocationListener
//        locationListener = LocationListener { location ->
//            Log.d("TAG", "LocationService:location $location")
//            // Handle new location updates here
//            location.let {
//                val latitude = location.latitude
//                val longitude = location.longitude
//                // Process location data as needed
//                Log.d("TAG", "LocationService:latitude {$latitude and $longitude}")
//                //showMessage("{$latitude and $longitude}")
//            }
//        }

        val filter = IntentFilter(PERMISSION_RESULT_ACTION)
        registerReceiver(permissionReceiver, filter)

        // Check if the required permission is granted
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            startLocationUpdates()
        } else {
            // Permission not granted, request it
            val intent = Intent(this, MainActivity::class.java)
            intent.action = PERMISSION_REQUEST_ACTION
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }

//        // Check if the required permission is granted
//        if (ActivityCompat.checkSelfPermission(
//                this,
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
//                this,
//                Manifest.permission.ACCESS_COARSE_LOCATION
//            ) == PackageManager.PERMISSION_GRANTED
//        ) {
//            startLocationUpdates()
//        } else {
//            // Permission not granted, request it
////            val intent = Intent(this, MainActivity::class.java)
////            intent.action = PERMISSION_REQUEST_ACTION
////            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
////            startActivity(intent)
//            showMessage("Permission not granted, request it")
//        }
    }

    private fun startLocationUpdates() {
        // Request location updates
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Permission not granted, request it
            val intent = Intent(this, MainActivity::class.java)
            intent.action = PERMISSION_REQUEST_ACTION
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            showMessage("Permission not granted, request it")
            return
        } else {
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                MIN_TIME_BETWEEN_UPDATES,
                MIN_DISTANCE_CHANGE_FOR_UPDATES,
                locationListener
            )
        }
    }

//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == REQUEST_CODE) {
//            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                startLocationUpdates()
//            } else {
//                // Permission denied, handle accordingly
//            }
//        }
//    }

//    override fun onDestroy() {
//        super.onDestroy()
//        // Stop location updates when the service is destroyed
//        locationManager.removeUpdates(locationListener)
//    }


    override fun onDestroy() {
        super.onDestroy()

        // Unregister the broadcast receiver
        unregisterReceiver(permissionReceiver)

        // ...
    }

    fun showMessage(message: String?) {
        Toast.makeText(this, message ?: "", Toast.LENGTH_SHORT).show()
    }
}
