package com.dev_hss.myserviceapp.workmanager

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters

class MyWorkerOld(appContext: Context, params: WorkerParameters) : Worker(appContext, params) {
    override fun doWork(): Result {


        Log.d("MyWorker", "Hello from MyWorker!")
        return Result.success()
    }
}