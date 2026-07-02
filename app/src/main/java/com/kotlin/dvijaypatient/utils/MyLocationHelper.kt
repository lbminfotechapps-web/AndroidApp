package com.kotlin.dvijaypatient.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.*
import com.kotlin.dvijaypatient.global.ClassGlobal

class MyLocationHelper(
    private val context: Context,
    private val listener: LocationUpdateListener
) {

    private val TAG = MyLocationHelper::class.java.simpleName

    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private lateinit var mLocationCallback: LocationCallback
    private lateinit var mLocationRequest: LocationRequest
    private lateinit var mGoogleApiClient: GoogleApiClient

    private val interval: Long = 30000
    private val fastestInterval: Long = 10000
    private val priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    private var Latitude = 0.0
    private var Longitude = 0.0

    init {
        buildGoogleApiClient()
    }

    private fun connectGoogleClient() {
        val googleAPI = GoogleApiAvailability.getInstance()
        val resultCode = googleAPI.isGooglePlayServicesAvailable(context)
        if (resultCode == ConnectionResult.SUCCESS) {
            mGoogleApiClient.connect()
        } else {
            val REQUEST_GOOGLE_PLAY_SERVICE = 988
            // googleAPI.getErrorDialog(context, resultCode, REQUEST_GOOGLE_PLAY_SERVICE)
        }
    }

    fun requestLocationUpdate() {
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback,   Looper.myLooper() ?: Looper.getMainLooper())
        }
    }

    fun removeLocationUpdate() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback)
        mGoogleApiClient.disconnect()
    }

    @Synchronized
    private fun buildGoogleApiClient() {
        listener.onStartLoc()
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        mGoogleApiClient = GoogleApiClient.Builder(context)
            .addConnectionCallbacks(object : GoogleApiClient.ConnectionCallbacks {
                override fun onConnected(bundle: Bundle?) {
                    mLocationRequest = LocationRequest.create().apply {
                        interval = this@MyLocationHelper.interval
                        fastestInterval = this@MyLocationHelper.fastestInterval
                        priority = this@MyLocationHelper.priority
                        smallestDisplacement = 0f
                    }

                    mLocationCallback = object : LocationCallback() {
                        override fun onLocationResult(locationResult: LocationResult) {
                            super.onLocationResult(locationResult)

                            Latitude = locationResult.lastLocation!!.latitude
                            Longitude = locationResult.lastLocation!!.longitude

                            if (Latitude == 0.0 && Longitude == 0.0) {
                                requestLocationUpdate()
                            } else {
                                Log.i(TAG, "onLocationResult: ${locationResult.lastLocation}")

                                listener.onUpdateLoc(locationResult.lastLocation!!)

                                val intent = Intent(ClassGlobal.LOCATION_UPDATE).apply {
                                    putExtra(ClassGlobal.LOCATION, locationResult.lastLocation)
                                }
                                context.sendBroadcast(intent)

                                mFusedLocationClient.removeLocationUpdates(mLocationCallback)
                            }
                        }
                    }

                    locationSettingsRequest()
                }

                override fun onConnectionSuspended(i: Int) {
                    connectGoogleClient()
                }
            })
            .addOnConnectionFailedListener { }
            .addApi(LocationServices.API)
            .build()

        connectGoogleClient()
    }

    private fun locationSettingsRequest() {
        val mSettingsClient = LocationServices.getSettingsClient(context)

        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(mLocationRequest)
        builder.setAlwaysShow(true)
        val mLocationSettingsRequest = builder.build()

        mSettingsClient.checkLocationSettings(mLocationSettingsRequest)
            .addOnSuccessListener {
                requestLocationUpdate()
            }
            .addOnFailureListener { e ->
                listener.onFailureLoc(e)
            }
            .addOnCanceledListener {
                Log.i(TAG, "Canceled No Thanks")
            }
    }

    interface LocationUpdateListener {
        fun onStartLoc()
        fun onUpdateLoc(location: Location)
        fun onFailureLoc(exception: Exception)
    }
}
