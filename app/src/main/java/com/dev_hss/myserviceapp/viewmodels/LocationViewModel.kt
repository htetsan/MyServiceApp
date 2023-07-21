package com.dev_hss.myserviceapp.viewmodels

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LocationViewModel : ViewModel() {
    private val mCurrentLocation = MutableLiveData<Location>()
    val currentLocation: LiveData<Location>
        get() = mCurrentLocation

    fun updateLocation(location: Location) {
        mCurrentLocation.value = location
    }
}