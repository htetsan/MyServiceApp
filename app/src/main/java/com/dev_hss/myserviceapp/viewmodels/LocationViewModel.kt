package com.dev_hss.myserviceapp.viewmodels

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LocationViewModel : ViewModel() {
    private val mCurrentLocation = MutableLiveData<Location>()
    val currentLocation: LiveData<Location>
        get() = mCurrentLocation

    private val mTriggerProcess = MutableLiveData<Boolean>()
    val triggerProcess: LiveData<Boolean>
        get() = mTriggerProcess

    fun updateLocation(location: Location) {
        mCurrentLocation.value = location
    }

    fun triggerProcess() {
        mTriggerProcess.value = true
    }

    fun processDone() {
        mTriggerProcess.value = false
    }
}