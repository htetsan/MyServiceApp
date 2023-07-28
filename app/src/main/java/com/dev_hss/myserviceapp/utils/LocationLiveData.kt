package com.dev_hss.myserviceapp.utils

import android.content.Context
import android.location.Location
import androidx.lifecycle.MutableLiveData

class LocationLiveData private constructor(private val context: Context) :
    MutableLiveData<Location>() {

    companion object {
        @Volatile
        private var instance: LocationLiveData? = null

        fun getInstance(context: Context): LocationLiveData {
            return instance ?: synchronized(this) {
                instance ?: LocationLiveData(context).also { instance = it }
            }
        }
    }
}