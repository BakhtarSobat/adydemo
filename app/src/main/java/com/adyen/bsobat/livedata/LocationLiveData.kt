package com.adyen.bsobat.livedata

import android.annotation.SuppressLint
import android.arch.lifecycle.LiveData
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.location.LocationManager.GPS_PROVIDER
import android.os.Bundle
import javax.inject.Inject

class LocationLiveData  @Inject constructor(val context: Context ): LiveData<Location>() {
    private val locationManager: LocationManager

    private val listener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            value = (location)
        }

        override fun onStatusChanged(s: String, i: Int, bundle: Bundle) {

        }

        override fun onProviderEnabled(s: String) {

        }

        override fun onProviderDisabled(s: String) {

        }
    }



    init {
        locationManager = context.getSystemService(
                Context.LOCATION_SERVICE) as LocationManager
    }

    @SuppressLint("MissingPermission")
    override fun onActive() {
        locationManager.requestLocationUpdates(GPS_PROVIDER, 1000, 0f, listener)
    }

    @SuppressLint("MissingPermission")
    override fun onInactive() {
        locationManager.removeUpdates(listener)
    }


}