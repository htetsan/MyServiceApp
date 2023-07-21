package com.dev_hss.myserviceapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.dev_hss.myserviceapp.databinding.ActivityMainBinding
import com.dev_hss.myserviceapp.utils.LocationService
import com.dev_hss.myserviceapp.viewmodels.LocationViewModel
import com.dev_hss.myserviceapp.workmanager.MyWorker
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {


    private lateinit var mBinding: ActivityMainBinding
    //private val locationViewModel: LocationViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        val serviceIntent = Intent(this, LocationService::class.java)

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
            //WorkManager.getInstance(this).enqueue(myWorkRequest)
            val periodicWork: PeriodicWorkRequest =
                PeriodicWorkRequest.Builder(MyWorker::class.java, 15, TimeUnit.MINUTES)
                    //.addTag(TAG)
                    .build()
            WorkManager.getInstance().enqueueUniquePeriodicWork(
                "Location",
                ExistingPeriodicWorkPolicy.REPLACE,
                periodicWork
            )
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