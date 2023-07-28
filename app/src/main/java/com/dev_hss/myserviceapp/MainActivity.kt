package com.dev_hss.myserviceapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.work.BackoffPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest.Companion.MIN_BACKOFF_MILLIS
import com.dev_hss.myserviceapp.databinding.ActivityMainBinding
import com.dev_hss.myserviceapp.utils.LocationLiveData
import com.dev_hss.myserviceapp.utils.LocationService
import com.dev_hss.myserviceapp.viewmodels.LocationViewModel
import com.dev_hss.myserviceapp.workmanager.MyWorker
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {


    private lateinit var mBinding: ActivityMainBinding
    private var TAG = "MainActivity"

    private val locationViewModel: LocationViewModel by viewModels()
    //private lateinit var locationViewModel: LocationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        val serviceIntent = Intent(this, LocationService::class.java)
        Log.d(TAG, "beforeViewModel")

        locationViewModel.currentLocation.observe(this) { location ->
            val latitude = location.latitude
            val longitude = location.longitude
            Log.d(TAG, "currentLocation: $latitude and $longitude")
        }

        val locationLiveData = LocationLiveData.getInstance(this)
        locationLiveData.observe(this, Observer { location ->
            // Update your UI or perform any other action with the updated location.
            // For example, you can update a TextView with the new location.
            // textView.text = location
            Log.d(TAG, "LocationLiveData: $location.")

        })

//        locationViewModel.currentLocation.observe(this, Observer { location ->
//            // Handle the updated location here and update your UI
//            // For example, you can display the latitude and longitude in a TextView
//            val latitude = location.latitude
//            val longitude = location.longitude
//            Log.d(TAG, "currentLocation: $latitude and $longitude")
//        })


        // Check if permission is already granted
        //testWithServices(serviceIntent)


        /*        val myWorkRequest = PeriodicWorkRequestBuilder<MyWorkerOld>(
                        1, TimeUnit.MINUTES
                    ).build()*/

        mBinding.startBtn.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                sendPermissionResult(true)
            } else {
                // Request the permission
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ),
                    1
                )
            }
//            //WorkManager.getInstance(this).enqueue(myWorkRequest)
//            val periodicWork: PeriodicWorkRequest =
//                PeriodicWorkRequest.Builder(MyWorker::class.java, 15, TimeUnit.MINUTES)
//                    //.addTag(TAG)
//                    .build()
//
//            WorkManager.getInstance().enqueueUniquePeriodicWork(
//                "Location",
//                ExistingPeriodicWorkPolicy.REPLACE,
//                periodicWork
//            )


            // Create a periodic OneTimeWorkRequest to get location updates every 15 minutes
            val workRequest = OneTimeWorkRequestBuilder<MyWorker>()
                .setInitialDelay(0, TimeUnit.SECONDS) // Delay before first execution
                .setBackoffCriteria(BackoffPolicy.LINEAR, MIN_BACKOFF_MILLIS, TimeUnit.MILLISECONDS)
                .addTag("locationUpdateWork") // Optional tag for identifying the WorkRequest
                .build()

            // Enqueue the OneTimeWorkRequest with WorkManager
            WorkManager.getInstance(this).enqueue(workRequest)

//            val outputData = WorkManager.getInstance(this).getWorkInfoByIdLiveData(workRequest.id)
//            outputData.observe(this) { workInfo ->
//                if (workInfo != null && workInfo.state == WorkInfo.State.SUCCEEDED) {
//                    val outputDataNew = workInfo.outputData
//                    val latitude = outputDataNew.getDouble("latitude", 0.0)
//                    val longitude = outputDataNew.getDouble("longitude", 0.0)
//
//                    // Update the LocationViewModel with the new location
//                    //locationViewModel.updateLocation(Location(latitude, longitude))
//                    Log.d(TAG, "onCreate: $latitude and $longitude")
//                    // Trigger the process in the MainActivity
//                    locationViewModel.triggerProcess()
//                }
//            }

//            locationViewModel.triggerProcess.observe(this, Observer { trigger ->
//                if (trigger) {
            // The process is triggered, you can call your desired method here

//                    locationViewModel.currentLocation.observe(this) { location ->
//                        val latitude = location.latitude
//                        val longitude = location.longitude
//                        Log.d(TAG, "currentLocation: $latitude and $longitude")
//                    }

            // Process is done, reset the trigger in the ViewModel
            //locationViewModel.processDone()
//                }
//            })
        }

        mBinding.stopBtn.setOnClickListener {
            //Log.d("TAG", "MyWorker: Cancel Work")
            //WorkManager.getInstance(this).cancelWorkById(myWorkRequest.id)
            Log.d("TAG", "MyWorker: Cancel all Work")
            WorkManager.getInstance(this).cancelAllWork()

        }


    }

    private fun testWithServices(serviceIntent: Intent) {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            sendPermissionResult(true)
        } else {
            // Request the permission
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                1
            )
        }


        serviceIntent.action = LocationService.ACTION_START
        startService(serviceIntent)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                sendPermissionResult(true)
            } else {
                sendPermissionResult(false)
            }
        }
    }

    private fun sendPermissionResult(granted: Boolean) {
        val intent = Intent(LocationService.PERMISSION_RESULT_ACTION)
        intent.putExtra(LocationService.EXTRA_PERMISSION_GRANTED, granted)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }
}